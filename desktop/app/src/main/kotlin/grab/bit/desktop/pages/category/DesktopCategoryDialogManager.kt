package grab.bit.desktop.pages.category

import grab.bit.shared.pagemanager.CategoryDialogManager
import grab.bit.shared.pages.category.CategoryComponent
import kotlinx.coroutines.flow.StateFlow

interface DesktopCategoryDialogManager : CategoryDialogManager {
    val openedCategoryDialogs: StateFlow<List<CategoryComponent>>
    fun closeCategoryDialog(categoryId: Long)
}
