package grab.bit.android.pages.home

import arrow.optics.optics
import grab.bit.android.pages.home.sections.sort.DownloadSortBy
import grab.bit.shared.ui.widget.sort.Sort
import kotlinx.serialization.Serializable

@optics
@Serializable
data class HomePageStateToPersist(
    val sortBy: Sort<DownloadSortBy> = Sort<DownloadSortBy>(DownloadSortBy.ActiveFirst, false),
    val manualOrder: List<Long> = emptyList(),
) {
    companion object {}
}
