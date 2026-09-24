package grab.bit.shared.downloaderinui.http

import grab.bit.shared.downloaderinui.BasicDownloadItem
import grab.bit.shared.downloaderinui.DownloaderInUi
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.edit.DownloadConflictDetector
import grab.bit.shared.downloaderinui.http.add.HttpNewDownloadUiChecker
import grab.bit.shared.downloaderinui.http.add.HttpLinkChecker
import grab.bit.shared.downloaderinui.http.add.HttpNewDownloadInputs
import grab.bit.shared.downloaderinui.http.edit.HttpEditDownloadChecker
import grab.bit.shared.downloaderinui.http.edit.HttpEditDownloadInputs
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.connection.response.HttpResponseInfo
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import grab.bit.downloader.downloaditem.http.HttpDownloadJob
import grab.bit.downloader.downloaditem.http.HttpDownloader
import grab.bit.downloader.downloaditem.http.IHttpDownloadCredentials
import grab.bit.downloader.monitor.ProcessingDownloadItemFactoryInputs
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.downloader.monitor.RangeBasedProcessingDownloadItemState
import grab.bit.downloader.monitor.UiRangedPart
import grab.bit.util.HttpUrlUtils
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class HttpDownloaderInUi(
    httpDownloader: HttpDownloader,
    private val sizeAndSpeedUnitProvider: SizeAndSpeedUnitProvider,
) : DownloaderInUi<HttpDownloadCredentials, HttpResponseInfo, DownloadSize.Bytes, HttpLinkChecker, HttpDownloadItem, HttpNewDownloadInputs, HttpEditDownloadInputs, HttpCredentialsToItemMapper, HttpDownloadJob, HttpDownloader>(
    downloader = httpDownloader
) {
    override fun createLinkChecker(initialCredentials: HttpDownloadCredentials): HttpLinkChecker {
        return HttpLinkChecker(
            initialCredentials,
            downloader.httpDownloaderClient,
        )
    }

    override fun newDownloadUiChecker(
        initialCredentials: HttpDownloadCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope,
    ): HttpNewDownloadUiChecker {
        return HttpNewDownloadUiChecker(
            initialCredentials = initialCredentials,
            linkCheckerFactory = this,
            initialFolder = initialFolder,
            initialName = initialName,
            downloadSystem = downloadSystem,
            scope = scope,
        )
    }

    override fun createNewDownloadInputs(
        initialCredentials: HttpDownloadCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope
    ): HttpNewDownloadInputs {
        val downloadUiChecker = newDownloadUiChecker(
            initialCredentials,
            initialFolder,
            initialName,
            downloadSystem,
            scope,
        )
        return HttpNewDownloadInputs(
            downloadUiChecker = downloadUiChecker,
            scope = scope,
            sizeAndSpeedUnitProvider = sizeAndSpeedUnitProvider
        )
    }

    override fun createEditDownloadInputs(
        currentDownloadItem: MutableStateFlow<HttpDownloadItem>,
        editedDownloadItem: MutableStateFlow<HttpDownloadItem>,
        conflictDetector: DownloadConflictDetector,
        scope: CoroutineScope
    ): HttpEditDownloadInputs {
        return HttpEditDownloadInputs(
            currentDownloadItem = currentDownloadItem,
            editedDownloadItem = editedDownloadItem,
            sizeAndSpeedUnitProvider = sizeAndSpeedUnitProvider,
            mapper = HttpCredentialsToItemMapper,
            conflictDetector = conflictDetector,
            scope = scope,
            linkCheckerFactory = this,
            editDownloadCheckerFactory = this,
        )
    }

    override fun acceptDownloadCredentials(item: IDownloadCredentials): Boolean {
        return item is IHttpDownloadCredentials
    }

    override fun supportsThisLink(link: String): Boolean {
        return HttpUrlUtils.isValidUrl(link)
    }

    override fun createMinimumCredentials(link: String): HttpDownloadCredentials {
        return HttpDownloadCredentials(link = link)
    }

    override fun createProcessingDownloadItemState(
        props: ProcessingDownloadItemFactoryInputs<HttpDownloadJob>
    ): ProcessingDownloadItemState {
        val downloadJob = props.downloadJob
        val downloadItem = downloadJob.downloadItem
        val downloadJobStatus = downloadJob.status.value
        val parts = downloadJob.getParts()
        val contentLength = downloadItem.contentLength
        return RangeBasedProcessingDownloadItemState(
            id = downloadItem.id,
            folder = downloadItem.folder,
            name = downloadItem.name,
            contentLength = contentLength,
            dateAdded = downloadItem.dateAdded,
            startTime = downloadItem.startTime ?: -1,
            completeTime = downloadItem.completeTime ?: -1,
            status = downloadJobStatus,
            saveLocation = downloadItem.name,
            parts = parts.map {
                UiRangedPart.fromPart(
                    part = it,
                    totalLength = contentLength,
                )
            },
            speed = props.speed,
            supportResume = downloadJob.supportsConcurrent,
            downloadLink = downloadItem.link,
            isWaiting = props.isWaiting,
        )
    }

    override fun createBareDownloadItem(
        credentials: HttpDownloadCredentials,
        basicDownloadItem: BasicDownloadItem
    ): HttpDownloadItem {
        return HttpDownloadItem.createWithCredentials(
            id = -1,
            credentials = credentials,
            folder = basicDownloadItem.folder,
            name = basicDownloadItem.name,
            contentLength = basicDownloadItem.contentLength,
            preferredConnectionCount = basicDownloadItem.preferredConnectionCount,
            speedLimit = basicDownloadItem.speedLimit,
            fileChecksum = basicDownloadItem.fileChecksum,
        )
    }

    override val name: StringSource = "HTTP".asStringSource()
    override fun createEditDownloadChecker(
        currentDownloadItem: MutableStateFlow<HttpDownloadItem>,
        editedDownloadItem: MutableStateFlow<HttpDownloadItem>,
        linkChecker: HttpLinkChecker,
        conflictDetector: DownloadConflictDetector,
        scope: CoroutineScope
    ): HttpEditDownloadChecker {
        return HttpEditDownloadChecker(
            currentDownloadItem = currentDownloadItem,
            editedDownloadItem = editedDownloadItem,
            linkChecker = linkChecker,
            conflictDetector = conflictDetector,
            scope = scope,
        )
    }
}
