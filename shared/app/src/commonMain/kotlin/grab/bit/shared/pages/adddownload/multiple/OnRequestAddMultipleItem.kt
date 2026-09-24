package grab.bit.shared.pages.adddownload.multiple

import grab.bit.shared.util.category.CategorySelectionMode
import grab.bit.downloader.NewDownloadItemProps
import kotlinx.coroutines.Deferred

fun interface OnRequestAddMultipleItem {
    operator fun invoke(
        items: List<NewDownloadItemProps>,
        queueId: Long?,
        categorySelectionMode: CategorySelectionMode?,
    ): Deferred<List<Long>>
}
