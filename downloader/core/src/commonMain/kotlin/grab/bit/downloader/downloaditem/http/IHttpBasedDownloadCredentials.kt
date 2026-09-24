package grab.bit.downloader.downloaditem.http

import grab.bit.downloader.downloaditem.IDownloadCredentials

interface IHttpBasedDownloadCredentials : IDownloadCredentials {
    val headers: Map<String, String>?
    val username: String?
    val password: String?
    val userAgent: String?
}
