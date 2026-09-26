package grab.bit.util

// Bulk naming template (gallery-dl recipe): "{host}/{name}_{id}.{ext}".
// Unknown keys stay untouched so typos are visible instead of silent.
//
// Batch renames use {n} (1-based position in the bulk list) and {host}:
// the default bulk mask turns "https://cdn.example/v/clip.mp4" at position 1
// into "clip_1.mp4".
object NameTemplate {
    const val DEFAULT_BATCH_TEMPLATE = "{name}_{n}.{ext}"
    private val keyRegex = Regex("""\{([a-zA-Z0-9_]+)}""")

    fun render(template: String, values: Map<String, String>): String {
        return keyRegex.replace(template) { match ->
            values[match.groupValues[1]] ?: match.value
        }
    }

    fun keys(template: String): Set<String> {
        return keyRegex.findAll(template).map { it.groupValues[1] }.toSet()
    }

    fun valuesForLink(link: String, index: Int): Map<String, String> {
        val host = HttpUrlUtils.getHost(link).orEmpty()
        val fileName = HttpUrlUtils.extractNameFromLink(link)
            ?: link.substringAfterLast('/').substringAfterLast('\\')
        return mapOf(
            "name" to fileName.substringBeforeLast('.', fileName),
            "ext" to fileName.substringAfterLast('.', ""),
            "host" to host,
            "n" to index.toString(),
        )
    }

    fun batchDisplayName(link: String, index: Int, template: String = DEFAULT_BATCH_TEMPLATE): String {
        // Links without an extension would render a stray trailing dot ("file_1.").
        return render(template, valuesForLink(link, index)).removeSuffix(".")
    }
}
