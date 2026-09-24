package grab.bit.shared.downloaderinui

import grab.bit.shared.downloaderinui.add.NewDownloadInputs
import grab.bit.shared.downloaderinui.edit.EditDownloadInputs
import grab.bit.downloader.Downloader
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.DownloadJob
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.monitor.CompletedDownloadItemState
import grab.bit.downloader.monitor.DownloadItemStateFactory
import grab.bit.downloader.monitor.ProcessingDownloadItemFactoryInputs
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import kotlin.reflect.KClass

typealias TADownloaderInUI = DownloaderInUi<
        IDownloadCredentials,
        IResponseInfo,
        DownloadSize,
        LinkChecker<IDownloadCredentials, IResponseInfo, DownloadSize>,
        IDownloadItem,
        NewDownloadInputs<IDownloadItem, IDownloadCredentials, IResponseInfo, DownloadSize, LinkChecker<IDownloadCredentials, IResponseInfo, DownloadSize>>,
        EditDownloadInputs<IDownloadItem, IDownloadCredentials, IResponseInfo, DownloadSize, LinkChecker<IDownloadCredentials, IResponseInfo, DownloadSize>, CredentialAndItemMapper<IDownloadCredentials, IDownloadItem>>,
        CredentialAndItemMapper<IDownloadCredentials, IDownloadItem>,
        DownloadJob,
        Downloader<IDownloadItem, DownloadJob, IDownloadCredentials>>

class DownloaderInUiRegistry
    : DownloadItemStateFactory<IDownloadItem, DownloadJob> {
    private val list = mutableListOf<TADownloaderInUI>()
    private val componentHashes = hashMapOf<Any, TADownloaderInUI>()
    fun add(downloaderInUi: DownloaderInUi<*, *, *, *, *, *, *, *, *, *>) {
        // the compiler gave me error when I add these two generics (TDownloadJob, TDownloader) into the DownloaderInUi
        @Suppress("UNCHECKED_CAST")
        val element = downloaderInUi as TADownloaderInUI
        list.add(element)
        getComponentsOf(element).forEach {
            componentHashes[it] = element
        }
    }

    fun remove(downloaderInUi: DownloaderInUi<*, *, *, *, *, *, *, *, *, *>) {
        list.remove(downloaderInUi)
        @Suppress("UNCHECKED_CAST")
        getComponentsOf(
            downloaderInUi as TADownloaderInUI
        ).forEach {
            componentHashes.remove(it)
        }
    }

    fun getDownloaderOf(downloadItem: IDownloadCredentials): TADownloaderInUI? {
        componentHashes.get(downloadItem::class)?.let {
            // fast path without iterating the list
            return it
        }
        // IDownloadCredentials is an intermediate interface so we should iterate the list instead
        return list.firstOrNull {
            it.acceptDownloadCredentials(downloadItem)
        }
    }

    private fun getDownloaderOf(downloadJob: DownloadJob): TADownloaderInUI? {
        return getDownloaderOf(downloadJob.downloadItem)
    }

    fun bestMatchForThisLink(link: String): TADownloaderInUI? {
        return list.firstOrNull {
            it.supportsThisLink(link)
        }
    }

    fun getAll(): List<TADownloaderInUI> {
        return list.toList()
    }


    override fun createProcessingDownloadItemState(
        props: ProcessingDownloadItemFactoryInputs<DownloadJob>,
    ): ProcessingDownloadItemState {
        val downloadJob = props.downloadJob
        return requireNotNull(getDownloaderOf(downloadJob)) {
            "there is no downloader in UI registered for this download job: ${downloadJob::class.qualifiedName}"
        }.createProcessingDownloadItemState(props)
    }

    override fun createCompletedDownloadItemState(downloadItem: IDownloadItem): CompletedDownloadItemState {
        return requireNotNull(getDownloaderOf(downloadItem)) {
            "there is no downloader in UI registered for this download item: ${downloadItem::class.qualifiedName}"
        }.createCompletedDownloadItemState(downloadItem)
    }

    companion object {
        private fun getComponentsOf(element: TADownloaderInUI): List<KClass<out Any>> {
            return listOf(
                element.downloader.downloadJobClass,
                element.downloader.downloadItemClass,
            )
        }
    }

}
