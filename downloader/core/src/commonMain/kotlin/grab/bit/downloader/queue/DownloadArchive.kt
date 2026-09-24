package grab.bit.downloader.queue

// No-re-download archive (gallery-dl --download-archive recipe): remembers
// finished URLs by hash so the same file is never fetched twice. Pure map;
// persistence wiring arrives in a later step.
class DownloadArchive {
    private val entries = LinkedHashMap<String, Long>()

    fun add(url: String, finishedAtMillis: Long) {
        entries[normalize(url)] = finishedAtMillis
    }

    fun contains(url: String): Boolean {
        return entries.containsKey(normalize(url))
    }

    fun remove(url: String) {
        entries.remove(normalize(url))
    }

    fun clear() {
        entries.clear()
    }

    fun pruneOlderThan(cutoffMillis: Long): Int {
        val old = entries.filterValues { it < cutoffMillis }.keys.toList()
        old.forEach(entries::remove)
        return old.size
    }

    val size: Int get() = entries.size

    companion object {
        fun normalize(url: String): String {
            return url.substringBefore('#').trim()
        }
    }
}
