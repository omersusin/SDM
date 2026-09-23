package ir.amirab.util

// Collects per-page media candidates fed from WebView request interception.
// Pure Kotlin so the grabber list logic stays unit-tested; the Android
// WebView hook only calls observe().
class PageMediaCollector {
    private val found = LinkedHashMap<String, MediaCandidate>()

    fun observe(url: String, mimeType: String? = null): MediaCandidate {
        val candidate = MediaSniffer.sniff(url, mimeType)
        if (candidate is MediaCandidate.Direct || candidate is MediaCandidate.Stream) {
            found.putIfAbsent(url, candidate)
        }
        return candidate
    }

    fun snapshot(): List<MediaCandidate> = found.values.toList()

    fun clear() {
        found.clear()
    }

    val count: Int get() = found.size
}
