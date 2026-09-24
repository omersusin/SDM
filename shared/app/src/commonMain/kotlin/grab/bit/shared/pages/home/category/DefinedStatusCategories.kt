package grab.bit.shared.pages.home.category

import grab.bit.resources.Res
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.downloader.downloaditem.DownloadStatus
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.util.compose.asStringSource

object DefinedStatusCategories {
    fun values() = listOf(All, Finished, Unfinished)


    val All = object : DownloadStatusCategoryFilter(
        Res.string.all.asStringSource(),
        MyIcons.folder,
    ) {
        override fun accept(iDownloadStatus: IDownloadItemState): Boolean = true
    }
    val Finished = DownloadStatusCategoryFilterByList(
        Res.string.finished.asStringSource(),
        MyIcons.folderFinished,
        listOf(DownloadStatus.Completed)
    )
    val Unfinished = DownloadStatusCategoryFilterByList(
        Res.string.Unfinished.asStringSource(),
        MyIcons.folderUnfinished,
        listOf(
            DownloadStatus.Error,
            DownloadStatus.Added,
            DownloadStatus.Paused,
            DownloadStatus.Downloading,
        )
    )
}
