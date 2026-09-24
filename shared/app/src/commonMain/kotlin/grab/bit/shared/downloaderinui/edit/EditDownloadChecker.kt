package grab.bit.shared.downloaderinui.edit

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class EditDownloadChecker<
        TDownloadItem : IDownloadItem,
        TCredentials : IDownloadCredentials,
        TResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfo, TDownloadSize>
        >(
    val currentDownloadItem: MutableStateFlow<TDownloadItem>,
    val editedDownloadItem: MutableStateFlow<TDownloadItem>,
    val linkChecker: TLinkChecker,
    val conflictDetector: DownloadConflictDetector,
    val scope: CoroutineScope,
) {
    abstract fun check()

    protected val _canEditResult = MutableStateFlow<CanEditDownloadResult>(CanEditDownloadResult.NothingChanged)
    val canEditResult = _canEditResult.asStateFlow()
    val canEdit = canEditResult.mapStateFlow {
        it is CanEditDownloadResult.CanEdit
    }
}
