package grab.bit.android.pages.add.single

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import grab.bit.android.pages.browser.BrowserActivity
import grab.bit.android.pages.category.CategorySheet
import grab.bit.android.pages.newqueue.NewQueueSheet
import grab.bit.android.pages.singledownload.SingleDownloadPageActivity
import grab.bit.android.util.ABDMAppManager
import grab.bit.android.util.AndroidDownloadItemOpener
import grab.bit.android.util.activity.ABDMActivity
import grab.bit.android.util.activity.HandleActivityEffects
import grab.bit.android.util.activity.RetainedComponentContainer
import grab.bit.android.util.activity.getSerializedExtra
import grab.bit.android.util.activity.putSerializedExtra
import grab.bit.android.util.pagemanager.AndroidDownloadErrorPageManager
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pages.adddownload.AddDownloadConfig
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.queue.QueueManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.milliseconds

class AddSingleDownloadActivity : ABDMActivity() {
    private val json: Json by inject()
    private val downloadSystem: DownloadSystem by inject()
    private val appManager: ABDMAppManager by inject()
    private val downloadItemOpener: AndroidDownloadItemOpener by inject()
    private val downloaderInUiRegistry: DownloaderInUiRegistry by inject()
    private val lastSavedLocationsStorage: ILastSavedLocationsStorage by inject()
    private val selectQueueStorage: ISelectQueueStorage by inject()
    private val queueManager: QueueManager by inject()
    private val categoryManager: CategoryManager by inject()
    private val iconProvider: FileIconProvider by inject()
    private val appContext: Context by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myRetainedComponent = myRetainedComponent {
            // TODO consider use a factory to create AndroidAddSingleDownloadComponent
            // we may create memory leaks if we accidentally pass Activity::this into the component lambdas
            val config = getComponentConfig(intent)
            val appManager = appManager
            val appContext = this@AddSingleDownloadActivity.appContext
            val scope = applicationScope
            val downloadItemOpener = downloadItemOpener
            val appSettingsStorage = appSettingsStorage
            val downloadSystem = downloadSystem
            val downloadErrorDialogManager = AndroidDownloadErrorPageManager(
                openIntent = {
                    sendEffect(RetainedComponentContainer.Effects.StartActivity(it))
                },
                json = json,
                context = applicationContext,
            )
            val closeAddDownloadDialog = {
                this@myRetainedComponent.finishActivityAction()
            }
            AndroidAddSingleDownloadComponent(
                ctx = it,
                onRequestClose = closeAddDownloadDialog,
                onRequestDownload = { item, categoryId ->
                    scope.launch {
                        val id = appManager.startNewDownload(item, categoryId).await()
                        if (appSettingsStorage.showDownloadProgressDialog.value) {
                            runCatching {
                                appContext.startActivity(
                                    SingleDownloadPageActivity.createIntent(
                                        appContext,
                                        id,
                                        true,
                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }.onFailure {
                                it.printStackTrace()
                            }
                        }
                    }
                },
                onRequestAddToQueue = { item, queue, category ->
                    appManager.addDownload(item, queue, category)
                },
                openExistingDownload = {
                    scope.launch {
                        downloadItemOpener.openDownloadItem(it)
                    }
                },
                updateExistingDownloadCredentials = { id, newCredentials, downloadJobExtraConfig ->
                    scope.launch {
                        downloadSystem.downloadManager.updateDownloadItem(
                            id = id,
                            downloadJobExtraConfig = downloadJobExtraConfig,
                            updater = {
                                it.withCredentials(newCredentials)
                            }
                        )
//                        openDownloadDialog(id)
                    }
                },
                downloadItemOpener = downloadItemOpener,
                downloadErrorDialogManager = downloadErrorDialogManager,
                lastSavedLocationsStorage = lastSavedLocationsStorage,
                selectQueueStorage = selectQueueStorage,
                importOptions = config.importOptions,
                id = config.id,
                downloaderInUi = downloaderInUiRegistry.getDownloaderOf(config.newDownload.credentials)!!,
                initialCredentials = config.newDownload,
                queueManager = queueManager,
                categoryManager = categoryManager,
                downloadSystem = downloadSystem,
                appSettings = appSettingsStorage,
                iconProvider = iconProvider,
                appScope = applicationScope,
                appRepository = appRepository,
                perHostSettingsManager = perHostSettingsManager,
            )
        }
        val addDownloadComponent = myRetainedComponent.component
        setABDMContent {
            myRetainedComponent.HandleActivityEffects()
            HandleEffects(addDownloadComponent) {
                if (it is AndroidAddSingleDownloadComponent.Effects.OpenInBrowser) {
                    startActivity(
                        BrowserActivity.createIntent(this, it.link)
                    )
                    finish()
                }
            }
            val dialogState = rememberResponsiveDialogState(false)
            dialogState.OnFullyDismissed {
                addDownloadComponent.onRequestClose()
            }
            LaunchedEffect(Unit) {
                // animate open after activity becomes fully open
                // is there a better way?
                delay(10.milliseconds)
                dialogState.show()
            }
            val onDismiss = { dialogState.hide() }
            ResponsiveDialog(
                dialogState,
                onDismiss
            ) {
                AddSingleDownloadPage(addDownloadComponent, onDismiss)
            }
            CategorySheet(
                categoryComponent = addDownloadComponent.categorySlot.rememberChild(),
                onDismiss = addDownloadComponent::closeCategoryDialog
            )
            NewQueueSheet(
                onQueueCreate = addDownloadComponent::createQueueWithName,
                isOpened = addDownloadComponent.showAddQueue.collectAsState().value,
                onCloseRequest = { addDownloadComponent.setShowAddQueue(false) },
            )
        }
    }

    private fun getComponentConfig(intent: Intent): AddDownloadConfig.SingleAddConfig {
        runCatching {
            with(json) {
                intent.getSerializedExtra<AddDownloadConfig.SingleAddConfig>(COMPONENT_CONFIG_KEY)
            }
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()?.let {
            return it
        }
        val link = intent.data?.toString().orEmpty()
        return AddDownloadConfig.SingleAddConfig(
            newDownload = AddDownloadCredentialsInUiProps(
                credentials = HttpDownloadCredentials(
                    link = link,
                )
            )
        )
    }

    companion object {
        const val COMPONENT_CONFIG_KEY = "ComponentConfig"
        const val LINK_KEY = "link"
        fun createIntent(
            context: Context,
            singleAddConfig: AddDownloadConfig.SingleAddConfig,
            json: Json,
        ): Intent {
            val intent = Intent(
                context,
                AddSingleDownloadActivity::class.java,
            )
            with(json) {
                intent.putSerializedExtra(COMPONENT_CONFIG_KEY, singleAddConfig)
            }
            return intent
        }
    }
}
