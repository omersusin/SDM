package grab.bit.downloader

import grab.bit.downloader.downloaditem.DownloadItemContext
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.utils.OnDuplicateStrategy

data class NewDownloadItemProps(
    val downloadItem: IDownloadItem,
    val extraConfig: DownloadJobExtraConfig?,
    val onDuplicateStrategy: OnDuplicateStrategy,
    val context: DownloadItemContext,
)
