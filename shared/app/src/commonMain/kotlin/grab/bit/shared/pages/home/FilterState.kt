package grab.bit.shared.pages.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import grab.bit.shared.pages.home.category.DefinedStatusCategories
import grab.bit.shared.pages.home.category.DownloadStatusCategoryFilter
import grab.bit.shared.util.category.Category
import grab.bit.downloader.db.QueueModel

@Stable
class FilterState {
    var textToSearch by mutableStateOf("")
    var typeCategoryFilter by mutableStateOf(null as Category?)
    var queueFilter by mutableStateOf(null as QueueModel?)
    var statusFilter by mutableStateOf<DownloadStatusCategoryFilter>(DefinedStatusCategories.All)
}
