package ir.amirab.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtractorRegistryTest {
    private val rules = listOf(
        ExtractorRule("example-gallery", Regex("""https?://example\.com/gallery/.*""")),
        ExtractorRule("example-file", Regex("""https?://example\.com/.*""")),
    )

    @Test
    fun firstMatchWins() {
        assertEquals(
            "example-gallery",
            ExtractorRegistry.find(rules, "https://example.com/gallery/42")?.name,
        )
        assertEquals(
            "example-file",
            ExtractorRegistry.find(rules, "https://example.com/other")?.name,
        )
    }

    @Test
    fun noMatchGivesNull() {
        assertNull(ExtractorRegistry.find(rules, "https://other.example/x"))
    }
}
