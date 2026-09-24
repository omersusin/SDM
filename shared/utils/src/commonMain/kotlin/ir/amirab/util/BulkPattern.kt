package ir.amirab.util

// Expands bulk URL patterns like "file[001-100].zip" (uGet recipe).
// Pure Kotlin; the add-links screen feeds the result into the pool/download.
object BulkPattern {
    private val rangeRegex = Regex("""\[(\d+)-(\d+)]""")

    fun hasPattern(pattern: String): Boolean {
        return rangeRegex.containsMatchIn(pattern)
    }

    fun expand(pattern: String): List<String>? {
        val match = rangeRegex.find(pattern) ?: return null
        val startStr = match.groupValues[1]
        val endStr = match.groupValues[2]
        val start = startStr.toLongOrNull() ?: return null
        val end = endStr.toLongOrNull() ?: return null
        if (end < start) return null
        val width = maxOf(startStr.length, endStr.length)
        // ponytail: hard cap — a typo like [1-99999999] must not OOM the app.
        if (end - start > 10_000) return null
        return (start..end).map { n ->
            pattern.replaceRange(
                match.range,
                n.toString().padStart(width, '0'),
            )
        }
    }
}
