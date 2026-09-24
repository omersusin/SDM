package grab.bit.desktop.pages.addDownload.single

import grab.bit.shared.downloaderinui.DownloaderInUi
import grab.bit.shared.pagemanager.CategoryDialogManager
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.pages.adddownload.ImportOptions
import grab.bit.shared.pages.adddownload.single.BaseAddSingleDownloadComponent
import grab.bit.shared.pages.adddownload.single.OnRequestAddSingleItem
import grab.bit.shared.pages.adddownload.single.OnRequestDownloadSingleItem
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.util.DownloadItemOpener
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.queue.QueueManager
import kotlinx.coroutines.CoroutineScope

class DesktopAddSingleDownloadComponent(
    ctx: ComponentContext,
    onRequestClose: () -> Unit,
    onRequestDownload: OnRequestDownloadSingleItem,
    onRequestAddToQueue: OnRequestAddSingleItem,
    openExistingDownload: (Long) -> Unit,
    updateExistingDownloadCredentials: (Long, IDownloadCredentials, DownloadJobExtraConfig?) -> Unit,
    downloadItemOpener: DownloadItemOpener,
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
    downloadErrorDialogManager: DownloadErrorDialogManager,
    private val categoryDialogManager: CategoryDialogManager,
) : BaseAddSingleDownloadComponent(
    ctx = ctx,
    onRequestClose = onRequestClose,
    onRequestDownload = onRequestDownload,
    onRequestAddToQueue = onRequestAddToQueue,
    openExistingDownload = openExistingDownload,
    updateExistingDownloadCredentials = updateExistingDownloadCredentials,
    downloadItemOpener = downloadItemOpener,
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
    downloadErrorDialogManager = downloadErrorDialogManager,
    appScope = appScope,
    appRepository = appRepository,
    perHostSettingsManager = perHostSettingsManager,
) {
    override fun getCategoryPageManager(): CategoryDialogManager {
        return categoryDialogManager
    }
}
