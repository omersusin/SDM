package grab.bit.shared.pages.home.category

import grab.bit.downloader.downloaditem.DownloadStatus
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.statusOrFinished
import grab.bit.util.compose.IconSource
import grab.bit.util.compose.StringSource

class DownloadStatusCategoryFilterByList(
    name: StringSource,
    icon: IconSource,
    val acceptedStatus: List<DownloadStatus>,
) : DownloadStatusCategoryFilter(name, icon) {
    override fun accept(iDownloadStatus: IDownloadItemState): Boolean {
        return iDownloadStatus
            .statusOrFinished()
            .asDownloadStatus() in acceptedStatus
    }
}
