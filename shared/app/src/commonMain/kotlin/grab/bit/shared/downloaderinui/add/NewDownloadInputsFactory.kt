package grab.bit.shared.downloaderinui.add

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.coroutines.CoroutineScope

interface NewDownloadInputsFactory<
        TDownloadItem : IDownloadItem,
        TCredentials : IDownloadCredentials,
        TResponseInfoType : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfoType, TDownloadSize>,
        TNewDownloadInputs : NewDownloadInputs<
                TDownloadItem,
                TCredentials,
                TResponseInfoType,
                TDownloadSize,
                TLinkChecker,
                >
        > {
    fun createNewDownloadInputs(
        initialCredentials: TCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope
    ): TNewDownloadInputs
}
