package grab.bit.downloader.downloaditem.http

import grab.bit.downloader.DownloadManager
import grab.bit.downloader.Downloader
import grab.bit.downloader.connection.HttpDownloaderClient
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

class HttpDownloader(
    httpDownloaderClient: Lazy<HttpDownloaderClient>
) : Downloader<HttpDownloadItem, HttpDownloadJob, HttpDownloadCredentials> {
    val httpDownloaderClient by httpDownloaderClient
    override fun createJob(
        item: HttpDownloadItem,
        downloadManager: DownloadManager,
    ): HttpDownloadJob {
        return HttpDownloadJob(
            item,
            downloadManager,
            httpDownloaderClient,
        )
    }

    override fun accept(item: IDownloadItem): Boolean {
        return item is HttpDownloadItem
    }

    override val downloadItemClass: KClass<HttpDownloadItem> = HttpDownloadItem::class
    override val downloadCredentialsClass: KClass<HttpDownloadCredentials> = HttpDownloadCredentials::class
    override val downloadJobClass: KClass<HttpDownloadJob> = HttpDownloadJob::class
    override val downloadItemSerializer: KSerializer<HttpDownloadItem> = HttpDownloadItem.serializer()
    override val downloadCredentialsSerializer: KSerializer<HttpDownloadCredentials> =
        HttpDownloadCredentials.serializer()
}
