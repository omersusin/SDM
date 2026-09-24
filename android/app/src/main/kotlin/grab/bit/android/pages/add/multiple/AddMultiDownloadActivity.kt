package grab.bit.android.pages.add.multiple

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.collectAsState
import grab.bit.android.pages.category.CategorySheet
import grab.bit.android.pages.newqueue.NewQueueSheet
import grab.bit.android.util.ABDMAppManager
import grab.bit.android.util.activity.ABDMActivity
import grab.bit.android.util.activity.HandleActivityEffects
import grab.bit.android.util.activity.getSerializedExtra
import grab.bit.android.util.activity.putSerializedExtra
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pages.adddownload.AddDownloadConfig
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.rememberChild
import grab.bit.downloader.queue.QueueManager
import kotlinx.serialization.json.Json
import org.koin.core.component.inject

class AddMultiDownloadActivity : ABDMActivity() {
    private val json: Json by inject()
    private val downloadSystem: DownloadSystem by inject()
    private val appManager: ABDMAppManager by inject()
    private val downloaderInUiRegistry: DownloaderInUiRegistry by inject()
    private val lastSavedLocationsStorage: ILastSavedLocationsStorage by inject()
    private val selectQueueStorage: ISelectQueueStorage by inject()
    private val queueManager: QueueManager by inject()
    private val categoryManager: CategoryManager by inject()
    private val iconProvider: FileIconProvider by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myRetainedComponent = myRetainedComponent {
            val config = getComponentConfig(intent)
            val appManager = appManager
            val closeAddDownloadDialog = {
                this@myRetainedComponent.finishActivityAction()
            }
            AndroidAddMultiDownloadComponent(
                ctx = it,
                onRequestClose = closeAddDownloadDialog,
                lastSavedLocationsStorage = lastSavedLocationsStorage,
                selectQueueStorage = selectQueueStorage,
                id = config.id,
                queueManager = queueManager,
                categoryManager = categoryManager,
                downloadSystem = downloadSystem,
                onRequestAddMultipleItem = { items, queueId, categorySelectionMode ->
                    appManager.addDownloads(
                        items = items,
                        categorySelectionMode = categorySelectionMode,
                        queueId = queueId,
                    )
                },
                onRequestDownloadMultipleItem = { items, categorySelectionMode ->
                    appManager.startNewDownloads(
                        items = items,
                        categorySelectionMode = categorySelectionMode,
                    )
                },
                perHostSettingsManager = perHostSettingsManager,
                fileIconProvider = iconProvider,
                appRepository = appRepository,
                downloaderInUiRegistry = downloaderInUiRegistry,
            ).apply { addItems(config.newDownloads) }
        }
        val addDownloadComponent = myRetainedComponent.component
        setABDMContent {
            myRetainedComponent.HandleActivityEffects()
            AddMultiItemPage(addDownloadComponent)
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

    private fun getComponentConfig(intent: Intent): AddDownloadConfig.MultipleAddConfig {
        runCatching {
            with(json) {
                intent.getSerializedExtra<AddDownloadConfig.MultipleAddConfig>(COMPONENT_CONFIG_KEY)
            }
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()?.let {
            return it
        }
        return AddDownloadConfig.MultipleAddConfig()
    }

    companion object {
        const val COMPONENT_CONFIG_KEY = "ComponentConfig"
        fun createIntent(
            context: Context,
            multipleAddConfig: AddDownloadConfig.MultipleAddConfig,
            json: Json,
        ): Intent {
            val intent = Intent(
                context,
                AddMultiDownloadActivity::class.java,
            )
            with(json) {
                intent.putSerializedExtra(COMPONENT_CONFIG_KEY, multipleAddConfig)
            }
            return intent
        }
    }
}
