package grab.bit.util

// Minimal EasyList matcher (1DM recipe: intercept + block in WebView).
// v1 covers "||domain^" rules — the bulk of network blocking. Regex rules,
// exceptions and cosmetic filters arrive later if pages prove noisy.
class AdBlockMatcher private constructor(
    private val blockedSuffixes: Set<String>,
) {
    fun isBlocked(url: String): Boolean {
        val host = runCatching {
            HttpUrlUtils.createURL(url).host
        }.getOrNull() ?: return false
        val lower = host.lowercase()
        return blockedSuffixes.any { suffix ->
            lower == suffix || lower.endsWith(".$suffix")
        }
    }

    val ruleCount: Int get() = blockedSuffixes.size

    companion object {
        fun parse(lines: List<String>): AdBlockMatcher {
            val suffixes = lines
                .map { it.trim() }
                .filter { it.startsWith("||") && it.contains('^') }
                .map { it.substringAfter("||").substringBefore('^').lowercase() }
                .filter { it.isNotEmpty() && !it.contains('*') && !it.contains('/') }
                .toSet()
            return AdBlockMatcher(suffixes)
        }
    }
}
