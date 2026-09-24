package grab.bit.downloader.utils

// Retry-After parsing (curl retry_sleep recipe): delay-seconds or HTTP-date.
// Pure Kotlin, tested; engine retry loop consumes it in a later step.
object RetryAfter {
    fun parseMillis(headerValue: String?, nowMillis: Long): Long? {
        val v = headerValue?.trim().takeUnless { it.isNullOrEmpty() } ?: return null
        v.toLongOrNull()?.let { secs ->
            return if (secs < 0) null else secs * 1000
        }
        return parseHttpDate(v)?.let { (it - nowMillis).coerceAtLeast(0) }
    }

    // Minimal RFC 1123 parser (IMF-fixdate only): "Sun, 06 Nov 1994 08:49:37 GMT".
    internal fun parseHttpDate(v: String): Long? {
        return runCatching {
            val parts = v.split(' ')
            if (parts.size != 6) return null
            val day = parts[1].toInt()
            val month = monthIndex(parts[2])
            val year = parts[3].toInt()
            val time = parts[4].split(':')
            if (time.size != 3) return null
            val epochDay = daysSinceEpoch(year, month, day)
            epochDay * 86_400_000L +
                time[0].toLong() * 3_600_000 +
                time[1].toLong() * 60_000 +
                time[2].toLong() * 1000
        }.getOrNull()
    }

    private fun monthIndex(m: String): Int {
        return listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
        ).indexOf(m).takeIf { it >= 0 } ?: throw IllegalArgumentException("bad month")
    }

    private fun daysSinceEpoch(year: Int, month: Int, day: Int): Long {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val era = y / 400
        val yoe = y - era * 400
        val mp = m - 3
        val doy = (153 * mp + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe - 719468L
    }
}
