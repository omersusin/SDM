package ir.amirab.downloader.torrent

data class MagnetLink(
    val infoHash: String,
    val name: String?,
    val trackers: List<String>,
)

// Minimal magnet: URI parser (entry point for torrent/magnet downloads).
// Pure Kotlin, no native code — the libtorrent4j session comes later.
object MagnetParser {
    fun parse(uri: String): MagnetLink? {
        return runCatching {
            val query = uri.substringAfter("magnet:?", "")
            if (query.isEmpty() || !uri.startsWith("magnet:?", ignoreCase = true)) {
                return null
            }
            val params = query.split('&')
            val xt = params.firstOrNull { it.startsWith("xt=") }
                ?.substringAfter("xt=")
                ?.let(::percentDecode)
                ?: return null
            val hash = xt.substringAfter("urn:btih:", "")
                .takeIf { it.isNotEmpty() } ?: return null
            if (!isValidHash(hash)) return null
            val name = params.firstOrNull { it.startsWith("dn=") }
                ?.substringAfter("dn=")
                ?.let(::percentDecode)
                ?.takeIf { it.isNotBlank() }
            val trackers = params
                .filter { it.startsWith("tr=") }
                .map { percentDecode(it.substringAfter("tr=")) }
                .filter { it.isNotBlank() }
            MagnetLink(infoHash = hash.lowercase(), name = name, trackers = trackers)
        }.getOrNull()
    }

    private fun isValidHash(hash: String): Boolean {
        val hex40 = hash.length == 40 && hash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
        val base32 = hash.length == 32 && hash.all { it in 'A'..'Z' || it in '2'..'7' }
        return hex40 || base32
    }

    // ponytail: hand-rolled decoder — java.net.URLEncoder is JVM-only,
    // commonMain needs this until ktor client (with proper URLs) arrives.
    internal fun percentDecode(s: String): String {
        val out = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '%' && i + 2 < s.length) {
                val hex = s.substring(i + 1, i + 3)
                val v = hex.toIntOrNull(16)
                if (v != null) {
                    out.append(v.toChar())
                    i += 3
                    continue
                }
            }
            out.append(if (c == '+') ' ' else c)
            i++
        }
        return out.toString()
    }
}
