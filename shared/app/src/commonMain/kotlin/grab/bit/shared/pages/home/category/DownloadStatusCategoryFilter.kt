package grab.bit.shared.pages.home.category

import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.util.compose.IconSource
import grab.bit.util.compose.StringSource

abstract class DownloadStatusCategoryFilter(
    val name: StringSource,
    val icon: IconSource,
) {
    abstract fun accept(iDownloadStatus: IDownloadItemState): Boolean
}
