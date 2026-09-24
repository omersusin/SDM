package grab.bit.shared.repository

import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.SupportedSizeUnits
import grab.bit.shared.util.AutoStartManager
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.autoremove.RemovedDownloadsFromDiskTracker
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.proxy.ProxyManager
import grab.bit.downloader.DownloadManager
import grab.bit.downloader.DownloadSettings
import grab.bit.downloader.SpeedProfile
import grab.bit.downloader.monitor.IDownloadMonitor
import grab.bit.util.datasize.ConvertSizeConfig
import grab.bit.util.flow.mapStateFlow
import grab.bit.util.flow.withPrevious
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

open class BaseAppRepository(
    protected val scope: CoroutineScope,
    protected val appSettings: BaseAppSettingsStorage,
    protected val proxyManager: ProxyManager,
    protected val downloadSystem: DownloadSystem,
    protected val downloadSettings: DownloadSettings,
    protected val removedDownloadsFromDiskTracker: RemovedDownloadsFromDiskTracker,
    protected val categoryManager: CategoryManager,
) : SizeAndSpeedUnitProvider {
    val theme = appSettings.theme
    val uiScale = appSettings.uiScale
    private val downloadManager: DownloadManager = downloadSystem.downloadManager
    private val downloadMonitor: IDownloadMonitor = downloadSystem.downloadMonitor

    val maxConcurrentDownloads = appSettings.maxConcurrentDownloads
    val speedLimiter = appSettings.speedLimit
    val speedProfile = appSettings.speedProfile
    val threadCount = appSettings.threadCount
    val dynamicPartCreation = appSettings.dynamicPartCreation
    val useServerLastModifiedTime = appSettings.useServerLastModifiedTime
    val appendExtensionToIncompleteDownloads = appSettings.appendExtensionToIncompleteDownloads
    val useSparseFileAllocation = appSettings.useSparseFileAllocation
    val maxDownloadRetryCount = appSettings.maxDownloadRetryCount
    val useAverageSpeed = appSettings.useAverageSpeed
    val saveLocation = appSettings.defaultDownloadFolder
    val apiEnabled = appSettings.apiEnabled
    val apiPort = appSettings.apiPort
    val apiAuthEnabled = appSettings.apiAuthEnabled
    val apiAuthKey = appSettings.apiAuthKey
    val trackDeletedFilesOnDisk = appSettings.trackDeletedFilesOnDisk

    override val sizeUnit = appSettings.sizeUnit.mapStateFlow {
        it.toConfig()
    }
    override val speedUnit = appSettings.speedUnit.mapStateFlow {
        it.toConfig()
    }


    fun setSizeUnit(sizeUnit: ConvertSizeConfig) {
        SupportedSizeUnits.fromConfig(sizeUnit)?.let {
            appSettings.sizeUnit.value = it
        }
    }

    fun setSpeedUnit(speedUnit: ConvertSizeConfig) {
        SupportedSizeUnits.fromConfig(speedUnit)?.let {
            appSettings.speedUnit.value = it
        }
    }

    fun setSpeedProfile(speedProfile: SpeedProfile) {
        appSettings.speedProfile.value = speedProfile
    }

    private fun applyEffectiveSpeedLimit() {
        val limit = if (speedProfile.value == SpeedProfile.HIGH) {
            speedLimiter.value
        } else {
            speedProfile.value.bytesPerSec
        }
        downloadSettings.globalSpeedLimit = limit
        downloadManager.limitGlobalSpeed(limit)
    }

    fun boot() {
        updateDownloadSettings()
    }

    private fun updateDownloadSettings() {
        downloadSettings.defaultThreadCount = threadCount.value
        downloadSettings.dynamicPartCreationMode = dynamicPartCreation.value
        downloadSettings.useServerLastModifiedTime = useServerLastModifiedTime.value
        downloadSettings.appendExtensionToIncompleteDownloads = appendExtensionToIncompleteDownloads.value
        downloadSettings.useSparseFileAllocation = useSparseFileAllocation.value
        downloadSettings.maxDownloadRetryCount = maxDownloadRetryCount.value
        downloadSettings.globalSpeedLimit = if (speedProfile.value == SpeedProfile.HIGH) {
            speedLimiter.value
        } else {
            speedProfile.value.bytesPerSec
        }
    }

    init {
        saveLocation
            .debounce(500.milliseconds)
            .withPrevious()
            .onEach { (oldDownloadFolder, newDownloadFolder) ->
                if (oldDownloadFolder == null) {
                    return@onEach
                }
                categoryManager.updateCategoryFoldersBasedOnDefaultDownloadFolder(
                    previousDownloadFolder = oldDownloadFolder,
                    currentDownloadFolder = newDownloadFolder,
                )
            }.launchIn(scope)
        //maybe its better to move this to another place
        appSettings.autoStartOnBoot
            .debounce(500.milliseconds)
            .onEach { enabled ->
                AutoStartManager.startOnBoot(enabled)
            }.launchIn(scope)
        speedLimiter
            .debounce(500.milliseconds)
            .onEach {
                applyEffectiveSpeedLimit()
            }.launchIn(scope)
        speedProfile
            .debounce(500.milliseconds)
            .onEach {
                applyEffectiveSpeedLimit()
            }.launchIn(scope)
        useAverageSpeed
            .debounce(500.milliseconds)
            .onEach {
                downloadMonitor.useAverageSpeed = it
            }.launchIn(scope)
        threadCount
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.defaultThreadCount = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        dynamicPartCreation
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.dynamicPartCreationMode = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        useServerLastModifiedTime
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.useServerLastModifiedTime = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        appendExtensionToIncompleteDownloads
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.appendExtensionToIncompleteDownloads = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        useSparseFileAllocation
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.useSparseFileAllocation = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        maxDownloadRetryCount
            .debounce(500.milliseconds)
            .onEach {
                downloadSettings.maxDownloadRetryCount = it
                downloadManager.reloadSetting()
            }.launchIn(scope)
        trackDeletedFilesOnDisk
            .debounce(500.milliseconds)
            .onEach { enabled ->
                if (enabled) {
                    removedDownloadsFromDiskTracker.removeDownloadsThatFilesAreMissing()
                    removedDownloadsFromDiskTracker.start()
                } else {
                    removedDownloadsFromDiskTracker.stop()
                }
            }.launchIn(scope)
        maxConcurrentDownloads
            .debounce(500.milliseconds)
            .onEach {
                downloadSystem.manualDownloadQueue.setMaxConcurrent(it)
            }.launchIn(scope)
    }
}
