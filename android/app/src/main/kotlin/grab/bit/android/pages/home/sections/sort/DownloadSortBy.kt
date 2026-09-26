package grab.bit.android.pages.home.sections.sort

import grab.bit.resources.Res
import grab.bit.shared.ui.widget.sort.ComparatorProvider
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.ActiveFirstSort
import grab.bit.downloader.monitor.statusOrFinished
import grab.bit.util.compose.IconSource
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class DownloadSortBy(
    val selector: (IDownloadItemState) -> Comparable<*>,
    val icon: IconSource,
    val name: StringSource,
) : ComparatorProvider<IDownloadItemState> {
    override fun comparator(): Comparator<IDownloadItemState> {
        return compareBy(selector)
    }

    @Serializable
    @SerialName("name")
    object Name : DownloadSortBy(
        selector = { it.name },
        icon = MyIcons.alphabet,
        name = Res.string.name.asStringSource(),
    )

    @Serializable
    @SerialName("dateAdded")
    object DataAdded : DownloadSortBy(
        selector = { it.dateAdded },
        icon = MyIcons.clock,
        name = Res.string.date_added.asStringSource(),
    )

    @Serializable
    @SerialName("status")
    data object Status : DownloadSortBy(
        selector = { it.statusOrFinished().order },
        icon = MyIcons.info,
        name = Res.string.status.asStringSource(),
    )

    @Serializable
    @SerialName("size")
    data object Size : DownloadSortBy(
        selector = { it.contentLength },
        icon = MyIcons.data,
        name = Res.string.size.asStringSource(),
    )

    @Serializable
    @SerialName("manual")
    data object Manual : DownloadSortBy(
        selector = { it.dateAdded },
        icon = MyIcons.dragAndDrop,
        name = Res.string.sort_manual.asStringSource(),
    )

    @Serializable
    @SerialName("activeFirst")
    data object ActiveFirst : DownloadSortBy(
        selector = { it.statusOrFinished().order },
        icon = MyIcons.download,
        name = Res.string.sort_active_first.asStringSource(),
    ) {
        override fun comparator(): Comparator<IDownloadItemState> {
            val order = ActiveFirstSort.comparator()
            return Comparator { a, b ->
                order.compare(
                    a.statusOrFinished().order to a.dateAdded,
                    b.statusOrFinished().order to b.dateAdded,
                )
            }
        }
    }
}
