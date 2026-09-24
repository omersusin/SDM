package grab.bit.util

// Bulk naming template (gallery-dl recipe): "{host}/{name}_{id}.{ext}".
// Unknown keys stay untouched so typos are visible instead of silent.
object NameTemplate {
    private val keyRegex = Regex("""\{([a-zA-Z0-9_]+)}""")

    fun render(template: String, values: Map<String, String>): String {
        return keyRegex.replace(template) { match ->
            values[match.groupValues[1]] ?: match.value
        }
    }

    fun keys(template: String): Set<String> {
        return keyRegex.findAll(template).map { it.groupValues[1] }.toSet()
    }
}
