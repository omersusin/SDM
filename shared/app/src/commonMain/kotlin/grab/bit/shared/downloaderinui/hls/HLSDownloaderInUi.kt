package grab.bit.shared.downloaderinui.hls

import grab.bit.shared.downloaderinui.BasicDownloadItem
import grab.bit.shared.downloaderinui.DownloaderInUi
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.edit.DownloadConflictDetector
import grab.bit.shared.downloaderinui.hls.add.HLSNewDownloadUIChecker
import grab.bit.shared.downloaderinui.hls.add.HLSNewDownloadInputs
import grab.bit.shared.downloaderinui.hls.edit.HLSEditDownloadChecker
import grab.bit.shared.downloaderinui.hls.edit.HLSEditDownloadInputs
import grab.bit.shared.downloaderinui.edit.EditDownloadChecker
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.downloader.downloaditem.hls.HLSDownloadItem
import grab.bit.downloader.downloaditem.hls.HLSDownloadJob
import grab.bit.downloader.downloaditem.hls.HLSDownloader
import grab.bit.downloader.downloaditem.hls.HLSResponseInfo
import grab.bit.downloader.downloaditem.hls.IHLSCredentials
import grab.bit.downloader.monitor.ProcessingDownloadItemFactoryInputs
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.util.HttpUrlUtils
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class HLSDownloaderInUi(
    downloader: HLSDownloader,
    private val sizeAndSpeedUnitProvider: SizeAndSpeedUnitProvider,
) : DownloaderInUi<
        HLSDownloadCredentials,
        HLSResponseInfo,
        DownloadSize.Duration,
        HLSLinkChecker,
        HLSDownloadItem,
        HLSNewDownloadInputs,
        HLSEditDownloadInputs,
        HlsItemToCredentialMapper,
        HLSDownloadJob,
        HLSDownloader
        >(downloader) {
    override fun newDownloadUiChecker(
        initialCredentials: HLSDownloadCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope
    ): HLSNewDownloadUIChecker {
        return HLSNewDownloadUIChecker(
            initCredentials = initialCredentials,
            linkCheckerFactory = this,
            initialFolder = initialFolder,
            initialName = initialName,
            downloadSystem = downloadSystem,
            scope = scope,
        )
    }

    override fun acceptDownloadCredentials(item: IDownloadCredentials): Boolean {
        return item is IHLSCredentials
    }

    override fun supportsThisLink(link: String): Boolean {
        return HttpUrlUtils.isValidUrl(link)
    }

    override fun createMinimumCredentials(link: String): HLSDownloadCredentials {
        return HLSDownloadCredentials(link = link)
    }

    override fun createBareDownloadItem(
        credentials: HLSDownloadCredentials,
        basicDownloadItem: BasicDownloadItem
    ): HLSDownloadItem {
        return HLSDownloadItem.createWithCredentials(
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

    override fun createProcessingDownloadItemState(
        props: ProcessingDownloadItemFactoryInputs<HLSDownloadJob>
    ): ProcessingDownloadItemState {
        return UiProcessingItemForHSLFactory.create(
            props,
        )
    }

    override val name: StringSource = "HLS".asStringSource()

    override fun createLinkChecker(initialCredentials: HLSDownloadCredentials): HLSLinkChecker {
        return HLSLinkChecker(
            credentials = initialCredentials,
            client = downloader.client
        )
    }

    override fun createEditDownloadChecker(
        currentDownloadItem: MutableStateFlow<HLSDownloadItem>,
        editedDownloadItem: MutableStateFlow<HLSDownloadItem>,
        linkChecker: HLSLinkChecker,
        conflictDetector: DownloadConflictDetector,
        scope: CoroutineScope
    ): EditDownloadChecker<HLSDownloadItem, HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration, HLSLinkChecker> {
        return HLSEditDownloadChecker(
            currentDownloadItem = currentDownloadItem,
            editedDownloadItem = editedDownloadItem,
            linkChecker = linkChecker,
            conflictDetector = conflictDetector,
            scope = scope,
        )
    }

    override fun createNewDownloadInputs(
        initialCredentials: HLSDownloadCredentials,
        initialFolder: String,
        initialName: String,
        downloadSystem: DownloadSystem,
        scope: CoroutineScope
    ): HLSNewDownloadInputs {
        return HLSNewDownloadInputs(
            newDownloadUiChecker(
                initialCredentials = initialCredentials,
                initialFolder = initialFolder,
                initialName = initialName,
                downloadSystem = downloadSystem,
                scope = scope,
            ),
            sizeAndSpeedUnitProvider,
            scope,
        )
    }

    override fun createEditDownloadInputs(
        currentDownloadItem: MutableStateFlow<HLSDownloadItem>,
        editedDownloadItem: MutableStateFlow<HLSDownloadItem>,
        conflictDetector: DownloadConflictDetector,
        scope: CoroutineScope
    ): HLSEditDownloadInputs {
        return HLSEditDownloadInputs(
            currentDownloadItem = currentDownloadItem,
            editedDownloadItem = editedDownloadItem,
            mapper = HlsItemToCredentialMapper(),
            conflictDetector = conflictDetector,
            scope = scope,
            linkCheckerFactory = this,
            editDownloadCheckerFactory = this,
            sizeAndSpeedUnitProvider = sizeAndSpeedUnitProvider,
        )
    }
}

