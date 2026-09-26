package grab.bit.downloader.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class PartCountTest {
    @Test
    fun preferredWins() {
        assertEquals(4, resolvePartCount(4, 8))
    }

    @Test
    fun nullPreferredFallsBackToDefault() {
        assertEquals(8, resolvePartCount(null, 8))
    }

    @Test
    fun zeroPreferredFallsBackToDefault() {
        assertEquals(8, resolvePartCount(0, 8))
    }

    @Test
    fun negativePreferredFallsBackToDefault() {
        assertEquals(8, resolvePartCount(-3, 8))
    }

    @Test
    fun brokenDefaultDegradesToSinglePart() {
        assertEquals(1, resolvePartCount(null, 0))
        assertEquals(1, resolvePartCount(0, -5))
    }

    @Test
    fun hugeValuesAreCapped() {
        assertEquals(MAX_PART_COUNT, resolvePartCount(Int.MAX_VALUE, 8))
        assertEquals(MAX_PART_COUNT, resolvePartCount(null, Int.MAX_VALUE))
    }

    @Test
    fun resolvedCountAlwaysSplitsCleanly() {
        // The clamp exists so splitToRange's require() can never fire from
        // persisted 0/negative counts: worst case is single-part.
        val ranges = splitToRange(
            size = 5L * 1024 * 1024,
            minPartSize = 1024 * 1024,
            maxPartCount = resolvePartCount(0, 0).toLong(),
        )
        assertEquals(1, ranges.size)
    }
}
