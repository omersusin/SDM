package grab.bit.downloader.downloaditem.hls

import grab.bit.downloader.DownloadManager
import grab.bit.downloader.Downloader
import grab.bit.downloader.connection.HttpDownloaderClient
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass


class HLSDownloader(
    client: Lazy<HttpDownloaderClient>
) : Downloader<HLSDownloadItem, HLSDownloadJob, HLSDownloadCredentials> {

    val client: HttpDownloaderClient by client

    override fun createJob(
        item: HLSDownloadItem,
        downloadManager: DownloadManager
    ): HLSDownloadJob {
        return HLSDownloadJob(
            downloadItem = item,
            downloadManager = downloadManager,
            client = client,
        )
    }

    override fun accept(item: IDownloadItem): Boolean {
        return item is HLSDownloadItem
    }

    override val downloadItemClass: KClass<HLSDownloadItem> = HLSDownloadItem::class
    override val downloadCredentialsClass: KClass<HLSDownloadCredentials> = HLSDownloadCredentials::class
    override val downloadJobClass: KClass<HLSDownloadJob> = HLSDownloadJob::class
    override val downloadItemSerializer: KSerializer<HLSDownloadItem> = HLSDownloadItem.serializer()
    override val downloadCredentialsSerializer: KSerializer<HLSDownloadCredentials> =
        HLSDownloadCredentials.serializer()
}
