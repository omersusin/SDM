package grab.bit.downloader

import grab.bit.downloader.downloaditem.DownloadJob
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

interface Downloader<
        TDownloadItem : IDownloadItem,
        TDownloadJob : DownloadJob,
        TDownloadCredentials : IDownloadCredentials,
        > {
    fun createJob(
        item: TDownloadItem,
        downloadManager: DownloadManager,
    ): TDownloadJob

    /**
     * accept if and only if [IDownloadItem] is [TDownloadItem]
     * */
    fun accept(item: IDownloadItem): Boolean
    val downloadItemClass: KClass<TDownloadItem>
    val downloadCredentialsClass: KClass<TDownloadCredentials>
    val downloadJobClass: KClass<TDownloadJob>

    val downloadItemSerializer: KSerializer<TDownloadItem>
    val downloadCredentialsSerializer: KSerializer<TDownloadCredentials>
}

