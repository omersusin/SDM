package ir.amirab.downloader.queue

data class CapturedLink(
    val url: String,
    val page: String?,
    val fileName: String?,
)

// Two-stage queue, stage 1: capture pool (JDownloader LinkGrabber recipe).
// Links land here first; the user selects a subset to actually download.
// Pure Kotlin; UI + download creation arrive in later steps.
class LinkPool {
    private val links = LinkedHashMap<String, CapturedLink>()
    private val selected = LinkedHashSet<String>()

    fun add(link: CapturedLink) {
        links.putIfAbsent(link.url, link)
        selected.add(link.url)
    }

    fun addAll(newLinks: List<CapturedLink>) {
        newLinks.forEach(::add)
    }

    fun toggleSelect(url: String) {
        if (!links.containsKey(url)) return
        if (!selected.remove(url)) {
            selected.add(url)
        }
    }

    fun selectedLinks(): List<CapturedLink> {
        return selected.mapNotNull { links[it] }
    }

    fun removeSelected(): List<CapturedLink> {
        val removed = selectedLinks()
        removed.forEach {
            links.remove(it.url)
            selected.remove(it.url)
        }
        return removed
    }

    fun clear() {
        links.clear()
        selected.clear()
    }

    val size: Int get() = links.size
    val selectedCount: Int get() = selected.size
}
