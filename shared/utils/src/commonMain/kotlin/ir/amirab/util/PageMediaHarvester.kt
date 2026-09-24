package ir.amirab.util

// Generic page-media harvester (gallery-dl generic.py recipe): scrape media
// tags from HTML, resolve to absolute URLs, dedupe. Fetching + naming
// template arrive in later steps.
object PageMediaHarvester {
    private val srcRegex = Regex(
        """<(?:img|video|source|audio)[^>]+?src\s*=\s*["']([^"']+)["']""",
        RegexOption.IGNORE_CASE,
    )

    fun harvest(html: String, pageUrl: String): List<String> {
        val page = runCatching { HttpUrlUtils.createURL(pageUrl) }.getOrNull()
            ?: return emptyList()
        val origin = "${page.scheme}://${page.host}"
        val dir = page.encodedPath.substringBeforeLast('/', "")
        return srcRegex.findAll(html)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() && !it.startsWith("data:", ignoreCase = true) }
            .mapNotNull { resolve(it, page.scheme, origin, dir) }
            .distinct()
            .toList()
    }

    private fun resolve(src: String, scheme: String, origin: String, dir: String): String? {
        return when {
            src.startsWith("http://") || src.startsWith("https://") -> src
            src.startsWith("//") -> "$scheme:$src"
            src.startsWith("/") -> origin + src
            src.startsWith("#") || src.startsWith("javascript:", ignoreCase = true) -> null
            else -> "$origin$dir/$src"
        }
    }
}
