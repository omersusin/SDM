package grab.bit.desktop.pages.addDownload.multiple

import grab.bit.shared.ui.widget.table.customtable.TableState
import grab.bit.shared.util.DownloadSystem
import grab.bit.desktop.repository.AppRepository
import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pagemanager.CategoryDialogManager
import grab.bit.shared.pages.adddownload.multiple.BaseAddMultiDownloadComponent
import grab.bit.shared.pages.adddownload.multiple.OnRequestAddMultipleItem
import grab.bit.shared.pages.adddownload.multiple.OnRequestDownloadMultipleItem
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.queue.QueueManager

class DesktopAddMultiDownloadComponent(
    ctx: ComponentContext,
    id: String,
    onRequestClose: () -> Unit,
    onRequestAddMultipleItem: OnRequestAddMultipleItem,
    onRequestDownloadMultipleItem: OnRequestDownloadMultipleItem,
    private val categoryDialogManager: CategoryDialogManager,
    lastSavedLocationsStorage: ILastSavedLocationsStorage,
    selectQueueStorage: ISelectQueueStorage,
    perHostSettingsManager: PerHostSettingsManager, downloadSystem: DownloadSystem,
    fileIconProvider: FileIconProvider,
    appRepository: AppRepository,
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
) {
    override fun getCategoryPageManager(): CategoryDialogManager {
        return categoryDialogManager
    }
    val tableState = TableState(
        cells = AddMultiItemTableCells.all(),
        forceVisibleCells = listOf(
            AddMultiItemTableCells.Check,
            AddMultiItemTableCells.Name,
        )
    )
}

