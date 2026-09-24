package grab.bit.shared.downloaderinui

import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials

interface LinkCheckerFactory<
        TCredentials : IDownloadCredentials,
        TResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfo, TDownloadSize>,
        > {
    fun createLinkChecker(
        initialCredentials: TCredentials
    ): TLinkChecker
}
