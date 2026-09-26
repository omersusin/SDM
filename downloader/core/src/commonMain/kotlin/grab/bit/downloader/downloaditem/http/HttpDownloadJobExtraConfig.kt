package grab.bit.downloader.downloaditem.http

import grab.bit.downloader.downloaditem.DownloadJobExtraConfig

data class HttpDownloadJobExtraConfig(
    val sequentialMode: Boolean = false,
) : DownloadJobExtraConfig
