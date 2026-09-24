package grab.bit.downloader.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SplitToRangeTest {
    private fun assertValid(size: Long, ranges: List<LongRange>) {
        require(ranges.isNotEmpty())
        val sorted = ranges.sortedBy { it.first }
        assertEquals(0L, sorted.first().first)
        assertEquals(size - 1, sorted.last().last)
        for (i in 1..<sorted.size) {
            assertEquals(sorted[i - 1].last + 1, sorted[i].first)
        }
        val total = sorted.sumOf { it.last - it.first + 1 }
        assertEquals(size, total)
    }

    @Test
    fun splitsUpTo256Parts() {
        val size = 10L * 1024 * 1024 * 1024
        val ranges = splitToRange(size, minPartSize = 1024 * 1024, maxPartCount = 256)
        assertEquals(256, ranges.size)
        assertValid(size, ranges)
    }

    @Test
    fun minSplitSizeGuardsSmallFiles() {
        // 5MB file with 20MB min part size stays single-part (aria2 min-split-size).
        val size = 5L * 1024 * 1024
        val ranges = splitToRange(size, minPartSize = 20L * 1024 * 1024, maxPartCount = 256)
        assertEquals(1, ranges.size)
        assertValid(size, ranges)
    }

    @Test
    fun singleByteFile() {
        val ranges = splitToRange(1, minPartSize = 1, maxPartCount = 256)
        assertEquals(listOf(0L..0L), ranges)
    }

    @Test
    fun singlePartRequested() {
        val size = 1000L
        val ranges = splitToRange(size, minPartSize = 1, maxPartCount = 1)
        assertEquals(listOf(0L..999L), ranges)
    }

    @Test
    fun unevenDivisionCoversEverything() {
        val size = 100L
        val ranges = splitToRange(size, minPartSize = 1, maxPartCount = 3)
        assertEquals(3, ranges.size)
        assertValid(size, ranges)
        assertTrue(ranges.all { it.last - it.first + 1 >= 33L })
    }
}
