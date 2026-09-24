package ir.amirab.util

// Site-extractor registry (gallery-dl/streamlink recipe): ordered rules,
// first pattern match wins. Extractors themselves arrive per-site later.
data class ExtractorRule(
    val name: String,
    val pattern: Regex,
)

object ExtractorRegistry {
    fun find(rules: List<ExtractorRule>, url: String): ExtractorRule? {
        return rules.firstOrNull { it.pattern.containsMatchIn(url) }
    }
}
