package grab.bit.android.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import grab.bit.android.pages.onboarding.permissions.PermissionManager
import grab.bit.android.service.DownloadSystemService
import grab.bit.android.service.KeepAliveServiceReason
import grab.bit.android.storage.AppSettingsStorage
import grab.bit.resources.Res
import grab.bit.shared.pagemanager.NotificationSender
import grab.bit.shared.ui.widget.MessageDialogType
import grab.bit.shared.ui.widget.NotificationManager
import grab.bit.shared.ui.widget.NotificationType
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.category.CategorySelectionMode
import grab.bit.shared.util.keepawake.KeepAwakeManager
import grab.bit.shared.util.notification.platformNotificationSound
import grab.bit.downloader.DownloadManagerEvents
import grab.bit.downloader.NewDownloadItemProps
import grab.bit.downloader.downloaditem.contexts.ResumedBy
import grab.bit.downloader.downloaditem.contexts.User
import grab.bit.downloader.queue.DefaultQueueInfo
import grab.bit.downloader.queue.activeQueuesFlow
import grab.bit.downloader.queue.queueModelsFlow
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.coroutines.launchWithDeferred
import grab.bit.util.guardedEntry
import grab.bit.util.suspendGuardedEntry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ABDMAppManager(
    private val context: Context,
    private val scope: CoroutineScope,
    val downloadSystem: DownloadSystem,
    val keepAwakeManager: KeepAwakeManager,
    val permissionManager: PermissionManager,
    val notificationManager: NotificationManager,
    val serviceNotificationManager: ABDMServiceNotificationManager,
    private val appSettingsStorage: AppSettingsStorage,
) : KoinComponent, NotificationSender {
    private var booted = guardedEntry()
    private var downloadSystemBooted = suspendGuardedEntry()
    fun isSoundAllowed(): Boolean {
        return appSettingsStorage.notificationSound.value
    }

    fun boot() {
        booted.action {
            registerAsFallbackNotification()
        }
    }

    fun canStartDownloadEngine(): Boolean {
        return permissionManager.isReady()
    }

    fun isDownloadSystemBooted(): Boolean {
        return downloadSystemBooted.isDone()
    }

    fun isBackgroundServiceRunning(): Boolean {
        return DownloadSystemService.isServiceRunning()
    }

    suspend fun startDownloadSystem() {
        downloadSystemBooted.action {
            appSettingsStorage.ensureVideoQualityMigrated()
            downloadSystem.boot()
            keepAwakeManager.boot()
            registerReceivers()
            registerWifiGate()
            registerDownloadEventNotifications()
        }
    }

    // Stops everything when WiFi is lost and WiFi-only is on. App-scoped
    // callback (process lifetime). Auto-resume on WiFi return is still open.
    private fun registerWifiGate() {
        val connectivity = context.getSystemService(ConnectivityManager::class.java) ?: return
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivity.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    if (appSettingsStorage.wifiOnlyDownloads.value) {
                        scope.launch {
                            runCatching {
                                downloadSystem.stopAnything()
                            }
                        }
                    }
                }
            }
        )
    }

    private var shouldShowToastsNotifications = MutableStateFlow(true)
    fun setNotificationsHandledInUi(shownInUi: Boolean) {
        shouldShowToastsNotifications.value = !shownInUi
    }

    private fun registerAsFallbackNotification(): () -> Unit {
        val context = context
        var lastNotificationSound = 0L
        val job = scope.headlessComposeRuntime {
            val scope = rememberCoroutineScope()
            val notifications by notificationManager.activeNotificationList.collectAsState()
            val shouldShowNotifications by shouldShowToastsNotifications.collectAsState()
            if (!shouldShowNotifications) {
                return@headlessComposeRuntime
            }
            notifications
                .firstOrNull()?.let { notification ->
                    DisposableEffect(notification) {
                        val title = notification.title.getString()
                        val description = notification.description.getString()
                        val iconText = when (notification.notificationType) {
                            NotificationType.Error -> "❌"
                            NotificationType.Info -> "ℹ\uFE0F"
                            is NotificationType.Loading -> "⏳"
                            NotificationType.Success -> "✔\uFE0F"
                            NotificationType.Warning -> "⚠\uFE0F"
                        }
                        val fullTitle = "$iconText $title - $description"
                        val toastJob = scope.launch(Dispatchers.Main) {
                            val toast = Toast.makeText(
                                context,
                                fullTitle,
                                Toast.LENGTH_LONG,
                            )
                            val now = System.currentTimeMillis()
                            val sinceLastSoundMillis = now - lastNotificationSound
                            // don't repeatedly play notification!
                            if (sinceLastSoundMillis > 5_000) {
                                runCatching {
                                    platformNotificationSound().play(notification.notificationType)
                                    lastNotificationSound = now
                                }.onFailure {
                                    it.printStackTrace()
                                }
                            }
                            toast.show()
                            currentCoroutineContext().job.invokeOnCompletion {
                                it?.let {
                                    toast.cancel()
                                }
                            }
                        }
                        onDispose {
                            scope.launch(Dispatchers.Main) {
                                toastJob.cancel()
                            }
                        }
                    }
                }
        }
        return { job.cancel() }
    }

    private fun registerDownloadEventNotifications() {
        downloadSystem.downloadEvents.onEach {
            onNewDownloadEvent(it)
        }.launchIn(scope)
    }

    private fun onNewDownloadEvent(it: DownloadManagerEvents) {
        if (it.context[ResumedBy]?.by !is User) {
            //only notify events that is started by user
            return
        }
        if (it is DownloadManagerEvents.OnJobCanceled) {
            val reason = downloadSystem.errorMapperRegistry.getReason(it.e)
            reason?.let { reason ->
                sendNotification(
                    "downloadId=${it.downloadItem.id}",
                    description = it.downloadItem.name.asStringSource(),
                    title = reason.title.asStringSource(),
                    type = NotificationType.Error,
                )
            }
        }
        if (it is DownloadManagerEvents.OnJobCompleted) {
            sendNotification(
                tag = "downloadId=${it.downloadItem.id}",
                description = it.downloadItem.name.asStringSource(),
                title = Res.string.finished.asStringSource(),
                type = NotificationType.Success,
            )
        }
    }

    suspend fun awaitDownloadEngineBoot() {
        downloadSystemBooted.awaitDone()
    }

    private fun registerReceivers(): () -> Unit {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    AndroidConstants.Intents.STOP_ACTION -> {
                        intent
                            .getLongExtra(AndroidConstants.Intents.TOGGLE_DOWNLOAD_ACTION_DOWNLOAD_ID, -1)
                            .takeIf { it > -1 }
                            ?.let {
                                scope.launch {
                                    downloadSystem.manualPause(it)
                                }
                            }
                    }

                    AndroidConstants.Intents.RESUME_ACTION -> {
                        intent
                            .getLongExtra(AndroidConstants.Intents.TOGGLE_DOWNLOAD_ACTION_DOWNLOAD_ID, -1)
                            .takeIf { it > -1 }
                            ?.let {
                                scope.launch {
                                    downloadSystem.userManualResume(it)
                                }
                            }
                    }

                    AndroidConstants.Intents.TOGGLE_ACTION -> {
                        intent
                            .getLongExtra(AndroidConstants.Intents.TOGGLE_DOWNLOAD_ACTION_DOWNLOAD_ID, -1)
                            .takeIf { it > -1 }
                            ?.let {
                                scope.launch {
                                    TODO("Toggle action not implemented yet")
                                }
                            }
                    }

                    AndroidConstants.Intents.STOP_ALL_ACTION -> {
                        scope.launch {
                            downloadSystem.stopAnything()
                        }
                    }

                    AndroidConstants.Intents.EXIT_ACTION -> {
                        val job = scope.launch {
                            downloadSystem.stopAnything()
                            stopOurService()
                        }
                        job.invokeOnCompletion {
                            exitProcess(0)
                        }
                    }
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter().apply {
                addAction(AndroidConstants.Intents.TOGGLE_ACTION)
                addAction(AndroidConstants.Intents.RESUME_ACTION)
                addAction(AndroidConstants.Intents.STOP_ACTION)
                addAction(AndroidConstants.Intents.STOP_ALL_ACTION)
                addAction(AndroidConstants.Intents.EXIT_ACTION)
            },
            ContextCompat.RECEIVER_EXPORTED,
        )
        return {
            for (receiver in listOf(receiver)) {
                context.unregisterReceiver(receiver)
            }
        }
    }

    suspend fun startOurService() {
        awaitDownloadEngineBoot()
        val intent = Intent(context, DownloadSystemService::class.java)
        withContext(Dispatchers.Main) {
            ContextCompat.startForegroundService(context, intent)
        }
        DownloadSystemService.awaitStart()
        autoStopService()
    }

    suspend fun stopOurService() {
        awaitDownloadEngineBoot()
        val intent = Intent(context, DownloadSystemService::class.java)
        withContext(Dispatchers.Main) {
            context.stopService(intent)
        }
    }

    fun startNewDownload(
        item: NewDownloadItemProps,
        categoryId: Long?,
    ): Deferred<Long> {
        return scope.launchWithDeferred {
            downloadSystem.addDownload(
                newDownload = item,
                queueId = DefaultQueueInfo.ID,
                categoryId = categoryId,
            ).also {
                downloadSystem.userManualResume(it)
            }
        }
    }

    fun startNewDownloads(
        items: List<NewDownloadItemProps>,
        categorySelectionMode: CategorySelectionMode?,
    ): Deferred<List<Long>> {
        return scope.launchWithDeferred {
            downloadSystem.addDownload(
                newItemsToAdd = items,
                categorySelectionMode = categorySelectionMode,
            ).also {
                it.forEach {
                    downloadSystem.userManualResume(it)
                }
            }
        }
    }

    fun addDownload(
        item: NewDownloadItemProps,
        queueId: Long?,
        categoryId: Long?,
    ): Deferred<Long> {
        return scope.launchWithDeferred {
            downloadSystem.addDownload(
                newDownload = item,
                queueId = queueId,
                categoryId = categoryId,
            )
        }
    }

    fun addDownloads(
        items: List<NewDownloadItemProps>,
        categorySelectionMode: CategorySelectionMode?,
        queueId: Long?,
    ): Deferred<List<Long>> {
        return scope.launchWithDeferred {
            downloadSystem.addDownload(
                newItemsToAdd = items,
                queueId = queueId,
                categorySelectionMode = categorySelectionMode,
            )
        }
    }

    override fun sendDialogNotification(
        title: StringSource,
        description: StringSource,
        type: MessageDialogType
    ) {
        sendNotification(
            title = title,
            description = description,
            type = when (type) {
                MessageDialogType.Info -> NotificationType.Info
                MessageDialogType.Error -> NotificationType.Error
                MessageDialogType.Success -> NotificationType.Success
                MessageDialogType.Warning -> NotificationType.Warning
            },
            tag = UUID.randomUUID(),
        )
    }

    override fun sendNotification(
        tag: Any,
        title: StringSource,
        description: StringSource,
        type: NotificationType
    ) {
        scope.launch {
            notificationManager.showNotification(
                title,
                description,
                delay = 5_000,
                type = type,
            )
        }
    }

    /**
     * in case of the notification permission is granted recently
     * we ask service notification manager to repost the notification
     */
    fun repostServiceNotification() {
        serviceNotificationManager.updateNotificationWithDefaultValue()
    }

    fun bootDownloadSystemAndService(): Boolean {
        if (isDownloadSystemBooted() && isBackgroundServiceRunning()) {
            return true
        }
        if (canStartDownloadEngine()) {
            scope.launch {
                startDownloadSystem()
                if (!isBackgroundServiceRunning()) {
                    startOurService()
                }
            }
            return true
        }
        return false
    }

    private val mustStayAliveFlow = combine(
        downloadSystem.downloadMonitor.activeDownloadCount,
        downloadSystem.queueManager.activeQueuesFlow(),
        downloadSystem.queueManager.queueModelsFlow(),
        ApplicationBackgroundTracker.isInBackgroundFlow,
    ) { activeDownloads, activeQueues, queueModels, isInBackground ->
        if (activeQueues.isNotEmpty()) {
            return@combine KeepAliveServiceReason.ActiveQueue(activeQueues.map { it.getQueueModel() })
        }
        if (activeDownloads > 0) {
            return@combine KeepAliveServiceReason.ActiveDownloads(activeDownloads)
        }
        val scheduledTimeQueue = queueModels.filter { it.scheduledTimes.enabledStartTime }
        if (scheduledTimeQueue.isNotEmpty()) {
            return@combine KeepAliveServiceReason.ScheduledQueues(scheduledTimeQueue)
        }
        if (!isInBackground) {
            return@combine KeepAliveServiceReason.AppIsInForeground
        }
        return@combine null
    }

    private var autoStopServiceJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun autoStopService() {
        synchronized(this) {
            autoStopServiceJob?.cancel()
            autoStopServiceJob = scope.launch {
                mustStayAliveFlow
                    .distinctUntilChanged()
                    .onEach {
                        serviceNotificationManager.setKeepAliveServiceReason(it)
                    }
                    .flatMapLatest {
                        if (it == null) flow {
                            // let it be null for 10 seconds
                            delay(10.seconds)
                            emit(Unit)
                        }
                        else emptyFlow()
                    }.first()
                stopOurService()
            }
        }
    }
}

