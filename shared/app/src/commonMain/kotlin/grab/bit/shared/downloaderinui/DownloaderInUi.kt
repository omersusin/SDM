package grab.bit.shared.downloaderinui

import grab.bit.shared.downloaderinui.add.NewDownloadInputs
import grab.bit.shared.downloaderinui.add.NewDownloadInputsFactory
import grab.bit.shared.downloaderinui.add.NewDownloadUiChecker
import grab.bit.shared.downloaderinui.edit.EditDownloadCheckerFactory
import grab.bit.shared.downloaderinui.edit.EditDownloadInputs
import grab.bit.shared.downloaderinui.edit.EditDownloadInputsFactory
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.Downloader
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.DownloadJob
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.monitor.CompletedDownloadItemState
import grab.bit.downloader.monitor.DownloadItemStateFactory
import grab.bit.downloader.monitor.ProcessingDownloadItemFactoryInputs
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.CoroutineScope

/**
 * This is a class that represent a downloader implementation details tight to the Application not just the downloader logic
 * including ui, component factories and every thing that app need work with
 */
abstract class DownloaderInUi<
        TCredentials : IDownloadCredentials,
        TResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfo, TDownloadSize>,
        TDownloadItem : IDownloadItem,
        TNewDownloadInputs : NewDownloadInputs<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>,
        TEditDownloadInputs : EditDownloadInputs<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker, TCredentialAndItemMapper>,
        TCredentialAndItemMapper : CredentialAndItemMapper<TCredentials, TDownloadItem>,
        TDownloadJob : DownloadJob,
        TDownloader : Downloader<TDownloadItem, TDownloadJob, TCredentials>
        >(
    val downloader: TDownloader
) :
    LinkCheckerFactory<TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>,
    EditDownloadCheckerFactory<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>,
    NewDownloadInputsFactory<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker, TNewDownloadInputs>,
    EditDownloadInputsFactory<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker, TCredentialAndItemMapper, TEditDownloadInputs>,
    DownloadItemStateFactory<TDownloadItem, TDownloadJob> {
    abstract fun newDownloadUiChecker(
        initialCredentials: TCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope,
    ): NewDownloadUiChecker<TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>


    abstract fun acceptDownloadCredentials(item: IDownloadCredentials): Boolean
    abstract fun supportsThisLink(link: String): Boolean
    abstract fun createMinimumCredentials(link: String): TCredentials
    abstract fun createBareDownloadItem(
        credentials: TCredentials,
        basicDownloadItem: BasicDownloadItem
    ): TDownloadItem

    abstract override fun createProcessingDownloadItemState(
        props: ProcessingDownloadItemFactoryInputs<TDownloadJob>,
    ): ProcessingDownloadItemState

    override fun createCompletedDownloadItemState(
        downloadItem: TDownloadItem,
    ): CompletedDownloadItemState {
        return CompletedDownloadItemState.fromDownloadItem(downloadItem)
    }

    abstract val name: StringSource
}
