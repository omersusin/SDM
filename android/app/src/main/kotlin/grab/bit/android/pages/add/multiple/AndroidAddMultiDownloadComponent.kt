package grab.bit.android.pages.add.multiple

import grab.bit.shared.action.createNewQueueAction
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pagemanager.CategoryDialogManager
import grab.bit.shared.pagemanager.NewQueuePageManager
import grab.bit.shared.pages.adddownload.multiple.BaseAddMultiDownloadComponent
import grab.bit.shared.pages.adddownload.multiple.OnRequestAddMultipleItem
import grab.bit.shared.pages.adddownload.multiple.OnRequestDownloadMultipleItem
import grab.bit.shared.pages.category.CategoryComponent
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import grab.bit.shared.util.subscribeAsStateFlow
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import grab.bit.downloader.queue.QueueManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

class AndroidAddMultiDownloadComponent(
    ctx: ComponentContext,
    id: String,
    onRequestClose: () -> Unit,
    onRequestAddMultipleItem: OnRequestAddMultipleItem,
    onRequestDownloadMultipleItem: OnRequestDownloadMultipleItem,
    lastSavedLocationsStorage: ILastSavedLocationsStorage,
    selectQueueStorage: ISelectQueueStorage,
    perHostSettingsManager: PerHostSettingsManager, downloadSystem: DownloadSystem,
    fileIconProvider: FileIconProvider,
    appRepository: BaseAppRepository,
    downloaderInUiRegistry: DownloaderInUiRegistry,
    queueManager: QueueManager,
    categoryManager: CategoryManager,
) : BaseAddMultiDownloadComponent(
    ctx = ctx,
    id = id,
    lastSavedLocationsStorage = lastSavedLocationsStorage,
    selectQueueStorage = selectQueueStorage,
    onRequestAddMultipleItem = onRequestAddMultipleItem,
    onRequestDownloadMultipleItem = onRequestDownloadMultipleItem,
    onRequestClose = onRequestClose,
    perHostSettingsManager = perHostSettingsManager,
    downloadSystem = downloadSystem,
    appRepository = appRepository,
    fileIconProvider = fileIconProvider,
    downloaderInUiRegistry = downloaderInUiRegistry,
    queueManager = queueManager,
    categoryManager = categoryManager,
), NewQueuePageManager, CategoryDialogManager {
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
    val newQueueAction = createNewQueueAction(
        scope,
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
    val showMoreOptions = _showMoreInputs.asStateFlow()
    fun setShowMoreOptions(value: Boolean) {
        _showMoreInputs.value = value
    }

    private val _showAddQueue = MutableStateFlow(false)
    val showAddQueue = _showAddQueue.asStateFlow()
    fun setShowAddQueue(value: Boolean) {
        _showAddQueue.value = value
    }

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
}

