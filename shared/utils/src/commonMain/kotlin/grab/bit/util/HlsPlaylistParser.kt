package grab.bit.util

// Minimal standalone HLS (m3u8) playlist parser, stdlib-only (no yt-dlp).
// Covers what the in-app grabber needs: master variants
// (BANDWIDTH/RESOLUTION/CODECS) + media segments (EXTINF durations,
// EXT-X-KEY method/URI/IV, EXT-X-MAP, ENDLIST). Kept standalone: variant /
// segment counts are exposed for the future download step, URI resolution
// happens there.
data class HlsVariant(
    val uri: String,
    val bandwidth: Long,
    val resolution: String?,
    val codecs: String?,
)

data class HlsKey(
    val method: String,
    val uri: String?,
    val iv: String?,
)

data class HlsSegment(
    val uri: String,
    val durationMs: Long,
    val key: HlsKey?,
    val mapUri: String?,
)

data class HlsMasterPlaylist(val variants: List<HlsVariant>) {
    val variantCount: Int get() = variants.size
}

data class HlsMediaPlaylist(
    val segments: List<HlsSegment>,
    val targetDurationMs: Long?,
    val endList: Boolean,
) {
    val segmentCount: Int get() = segments.size
    val totalDurationMs: Long get() = segments.sumOf { it.durationMs }
}

sealed interface HlsPlaylist {
    data class Master(val playlist: HlsMasterPlaylist) : HlsPlaylist

    data class Media(val playlist: HlsMediaPlaylist) : HlsPlaylist

    data object Invalid : HlsPlaylist
}

// ponytail: plain line scan, no regex heroics; playlists are small and the
// parser must never throw on malformed input.
object HlsPlaylistParser {
    fun parse(content: String): HlsPlaylist {
        val lines = normalize(content)
        if (lines.firstOrNull() != "#EXTM3U") return HlsPlaylist.Invalid
        return if (lines.any { it.startsWith("#EXT-X-STREAM-INF") }) {
            HlsPlaylist.Master(parseMaster(lines))
        } else {
            HlsPlaylist.Media(parseMedia(lines))
        }
    }

    fun parseMaster(content: String): HlsMasterPlaylist {
        return parseMaster(normalize(content))
    }

    fun parseMedia(content: String): HlsMediaPlaylist {
        return parseMedia(normalize(content))
    }

    private fun normalize(content: String): List<String> {
        return content.lines()
            .map { it.trim().trimStart('\uFEFF') }
            .filter { it.isNotEmpty() }
    }

    private fun parseMaster(lines: List<String>): HlsMasterPlaylist {
        val variants = mutableListOf<HlsVariant>()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.startsWith("#EXT-X-STREAM-INF:")) {
                val attrs = parseAttributes(line.substringAfter(':'))
                val uri = lines.getOrNull(i + 1)?.takeIf { !it.startsWith("#") }
                if (uri != null) {
                    variants.add(
                        HlsVariant(
                            uri = uri,
                            bandwidth = attrs["BANDWIDTH"]?.toLongOrNull() ?: 0L,
                            resolution = attrs["RESOLUTION"],
                            codecs = attrs["CODECS"],
                        )
                    )
                }
            }
            i++
        }
        return HlsMasterPlaylist(variants)
    }

    private fun parseMedia(lines: List<String>): HlsMediaPlaylist {
        val segments = mutableListOf<HlsSegment>()
        var targetDurationMs: Long? = null
        var endList = false
        var currentKey: HlsKey? = null
        var currentMap: String? = null
        var pendingDurationMs: Long? = null
        for (line in lines) {
            when {
                line.startsWith("#EXT-X-TARGETDURATION:") -> {
                    targetDurationMs = line.substringAfter(':').trim()
                        .toLongOrNull()?.times(1000)
                }

                line.startsWith("#EXT-X-KEY:") -> {
                    val attrs = parseAttributes(line.substringAfter(':'))
                    val method = attrs["METHOD"].orEmpty()
                    currentKey = if (method == "NONE" || method.isEmpty()) {
                        null
                    } else {
                        HlsKey(method = method, uri = attrs["URI"], iv = attrs["IV"])
                    }
                }

                line.startsWith("#EXT-X-MAP:") -> {
                    currentMap = parseAttributes(line.substringAfter(':'))["URI"]
                }

                line.startsWith("#EXTINF:") -> {
                    pendingDurationMs = line.substringAfter(':').substringBefore(',')
                        .trim().toDoubleOrNull()?.times(1000)?.toLong() ?: 0L
                }

                line == "#EXT-X-ENDLIST" -> endList = true

                !line.startsWith("#") -> {
                    if (pendingDurationMs != null) {
                        segments.add(
                            HlsSegment(
                                uri = line,
                                durationMs = pendingDurationMs,
                                key = currentKey,
                                mapUri = currentMap,
                            )
                        )
                        pendingDurationMs = null
                    }
                }
            }
        }
        return HlsMediaPlaylist(
            segments = segments,
            targetDurationMs = targetDurationMs,
            endList = endList,
        )
    }

    private fun parseAttributes(raw: String): Map<String, String> {
        val out = LinkedHashMap<String, String>()
        var inQuotes = false
        val current = StringBuilder()
        val parts = mutableListOf<String>()
        for (ch in raw) {
            when {
                ch == '"' -> {
                    inQuotes = !inQuotes
                    current.append(ch)
                }

                ch == ',' && !inQuotes -> {
                    parts.add(current.toString())
                    current.clear()
                }

                else -> current.append(ch)
            }
        }
        parts.add(current.toString())
        for (part in parts) {
            val key = part.substringBefore('=').trim().takeIf { it.isNotEmpty() } ?: continue
            val value = unquote(part.substringAfter('=', "").trim()).takeIf { it.isNotEmpty() }
            if (value != null) out[key] = value
        }
        return out
    }

    private fun unquote(value: String): String {
        return if (value.length >= 2 && value.startsWith('"') && value.endsWith('"')) {
            value.substring(1, value.length - 1)
        } else {
            value
        }
    }
}
