package grab.bit.shared.downloaderinui.edit

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface EditDownloadCheckerFactory<
        TDownloadItem : IDownloadItem,
        TCredentials : IDownloadCredentials,
        TResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfo, TDownloadSize>> {
    fun createEditDownloadChecker(
        currentDownloadItem: MutableStateFlow<TDownloadItem>,
        editedDownloadItem: MutableStateFlow<TDownloadItem>,
        linkChecker: TLinkChecker,
        conflictDetector: DownloadConflictDetector,
        scope: CoroutineScope,
    ): EditDownloadChecker<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>
}
