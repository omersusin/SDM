package grab.bit.shared.util.category

interface ICategoryItemProvider {
    suspend fun getAll(): List<CategoryItemWithId>
}
