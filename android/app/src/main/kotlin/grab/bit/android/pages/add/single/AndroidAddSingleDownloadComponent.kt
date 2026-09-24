package grab.bit.android.pages.add.single

import grab.bit.shared.action.createNewQueueAction
import grab.bit.shared.downloaderinui.DownloaderInUi
import grab.bit.shared.pagemanager.CategoryDialogManager
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.pagemanager.NewQueuePageManager
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.pages.adddownload.ImportOptions
import grab.bit.shared.pages.adddownload.single.BaseAddSingleDownloadComponent
import grab.bit.shared.pages.adddownload.single.OnRequestAddSingleItem
import grab.bit.shared.pages.adddownload.single.OnRequestDownloadSingleItem
import grab.bit.shared.pages.category.CategoryComponent
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.DownloadItemOpener
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import grab.bit.shared.util.subscribeAsStateFlow
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.queue.QueueManager
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class AndroidAddSingleDownloadComponent(
    ctx: ComponentContext,
    onRequestClose: () -> Unit,
    onRequestDownload: OnRequestDownloadSingleItem,
    onRequestAddToQueue: OnRequestAddSingleItem,
    openExistingDownload: (Long) -> Unit,
    updateExistingDownloadCredentials: (Long, IDownloadCredentials, DownloadJobExtraConfig?) -> Unit,
    downloadItemOpener: DownloadItemOpener,
    downloadErrorDialogManager: DownloadErrorDialogManager,
    lastSavedLocationsStorage: ILastSavedLocationsStorage,
    selectQueueStorage: ISelectQueueStorage,
    queueManager: QueueManager,
    categoryManager: CategoryManager,
    downloadSystem: DownloadSystem,
    appSettings: BaseAppSettingsStorage,
    iconProvider: FileIconProvider,
    appScope: CoroutineScope,
    appRepository: BaseAppRepository,
    perHostSettingsManager: PerHostSettingsManager,
    importOptions: ImportOptions,
    id: String,
    downloaderInUi: DownloaderInUi<IDownloadCredentials, *, *, *, *, *, *, *, *, *>,
    initialCredentials: AddDownloadCredentialsInUiProps,
) : BaseAddSingleDownloadComponent(
    ctx = ctx,
    onRequestClose = onRequestClose,
    onRequestDownload = onRequestDownload,
    onRequestAddToQueue = onRequestAddToQueue,
    openExistingDownload = openExistingDownload,
    updateExistingDownloadCredentials = updateExistingDownloadCredentials,
    downloadItemOpener = downloadItemOpener,
    downloadErrorDialogManager = downloadErrorDialogManager,
    lastSavedLocationsStorage = lastSavedLocationsStorage,
    selectQueueStorage = selectQueueStorage,
    importOptions = importOptions,
    id = id,
    downloaderInUi = downloaderInUi,
    initialCredentials = initialCredentials,
    queueManager = queueManager,
    categoryManager = categoryManager,
    downloadSystem = downloadSystem,
    appSettings = appSettings,
    iconProvider = iconProvider,
    appScope = appScope,
    appRepository = appRepository,
    perHostSettingsManager = perHostSettingsManager,
), CategoryDialogManager, NewQueuePageManager {
    val categoryComponentNavigation = SlotNavigation<Long>()
    val categorySlot = childSlot(
        source = categoryComponentNavigation,
        childFactory = { config, ctx ->
            CategoryComponent(
                ctx = ctx,
                id = config,
                close = ::closeCategoryDialog,
                submit = { submittedCategory ->
                    if (submittedCategory.id < 0) {
                        categoryManager.addCustomCategory(submittedCategory)
                    } else {
                        categoryManager.updateCategory(
                            submittedCategory.id
                        ) {
                            submittedCategory.copy(
                                items = it.items
                            )
                        }
                    }
                    closeCategoryDialog()
                },
            )
        },
        serializer = Long.serializer(),
    ).subscribeAsStateFlow()
    val newQueuesAction = createNewQueueAction(
        appScope,
        this,
    )

    override fun openCategoryDialog(categoryId: Long) {
        scope.launch {
            categoryComponentNavigation.activate(categoryId)
        }
    }

    override fun closeCategoryDialog() {
        scope.launch {
            categoryComponentNavigation.dismiss()
        }
    }

    override fun getCategoryPageManager(): CategoryDialogManager {
        return this
    }

    private val _showMoreInputs = MutableStateFlow(false)
    val showMoreInputs = _showMoreInputs.asStateFlow()
    fun setShowMoreInputs(value: Boolean) {
        _showMoreInputs.value = value
    }

    private val _showAddQueue = MutableStateFlow(false)
    val showAddQueue = _showAddQueue.asStateFlow()
    fun setShowAddQueue(value: Boolean) {
        _showAddQueue.value = value
    }

    val isWebPage = downloadChecker
        .responseInfo
        .mapStateFlow { it?.isWebPage ?: false }

    fun createQueueWithName(name: String) {
        scope.launch { queueManager.addQueue(name) }
        setShowAddQueue(false)
    }

    override fun closeNewQueueDialog() {
        setShowAddQueue(false)
    }

    override fun openNewQueueDialog() {
        setShowAddQueue(true)
    }

    fun onRequestOpenLinkInBrowser() {
        sendEffect(
            Effects.OpenInBrowser(
                downloadChecker.credentials.value.link
            )
        )
    }

    sealed interface Effects : BaseAddSingleDownloadComponent.Effects.Platform {
        data class OpenInBrowser(val link: String) : Effects
    }
}
