package grab.bit.shared.util.category

import grab.bit.downloader.DownloadManager

class DownloadManagerCategoryItemProvider(
    private val dowManager: DownloadManager,
) : ICategoryItemProvider {
    override suspend fun getAll(): List<CategoryItemWithId> {
        return dowManager.getDownloadList().map {
            CategoryItemWithId(
                id = it.id,
                fileName = it.name,
                url = it.link
            )
        }
    }
}
