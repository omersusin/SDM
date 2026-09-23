package ir.amirab.util

enum class StreamKind {
    HLS,
    DASH,
}

sealed interface MediaCandidate {
    data class Direct(val url: String, val fileName: String?) : MediaCandidate

    data class Stream(val url: String, val kind: StreamKind) : MediaCandidate

    data object NotMedia : MediaCandidate
}

// ponytail: extension+mime heuristic only; fragment filtering and
// content-sniffing come in later grabber steps if pages prove noisy.
object MediaSniffer {
    private val directExtensions = setOf(
        "mp4", "m4v", "mkv", "webm", "mov", "avi", "flv", "wmv", "ts",
        "mp3", "m4a", "aac", "ogg", "oga", "opus", "flac", "wav", "weba",
    )

    fun sniff(url: String, mimeType: String? = null): MediaCandidate {
        return runCatching {
            if (url.isBlank()) return MediaCandidate.NotMedia
            sniffByExtension(url)?.let { return it }
            sniffByMime(url, mimeType)?.let { return it }
            MediaCandidate.NotMedia
        }.getOrDefault(MediaCandidate.NotMedia)
    }

    private fun sniffByExtension(url: String): MediaCandidate? {
        val path = url.substringBefore('?').substringBefore('#')
        val lastSegment = path.substringAfterLast('/').ifBlank { return null }
        // ponytail: plain substring ops, no HttpUrl parse — sniffing must
        // never throw on the weird URLs pages actually contain.
        val ext = lastSegment.substringAfterLast('.', "").lowercase()
        if (ext.isEmpty() || ext == lastSegment.lowercase()) return null
        return when (ext) {
            "m3u8" -> MediaCandidate.Stream(url, StreamKind.HLS)
            "mpd" -> MediaCandidate.Stream(url, StreamKind.DASH)
            in directExtensions -> MediaCandidate.Direct(url, lastSegment)
            else -> null
        }
    }

    private fun sniffByMime(url: String, mimeType: String?): MediaCandidate? {
        val mime = mimeType?.substringBefore(';')?.trim()?.lowercase()
            .takeUnless { it.isNullOrEmpty() } ?: return null
        return when {
            mime == "application/x-mpegurl" || mime == "application/vnd.apple.mpegurl" ->
                MediaCandidate.Stream(url, StreamKind.HLS)

            mime == "application/dash+xml" ->
                MediaCandidate.Stream(url, StreamKind.DASH)

            mime.startsWith("video/") || mime.startsWith("audio/") -> {
                val name = url.substringBefore('?').substringBefore('#')
                    .substringAfterLast('/').takeIf { it.isNotBlank() }
                MediaCandidate.Direct(url, name)
            }

            else -> null
        }
    }
}
