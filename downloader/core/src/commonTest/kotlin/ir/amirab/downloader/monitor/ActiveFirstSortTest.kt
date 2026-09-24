package ir.amirab.downloader.monitor

import kotlin.test.Test
import kotlin.test.assertEquals

class ActiveFirstSortTest {
    @Test
    fun activeFirstThenNewest() {
        val items = listOf(
            3 to 300L,
            0 to 100L,
            2 to 200L,
            0 to 400L,
        )
        assertEquals(
            listOf(0 to 400L, 0 to 100L, 2 to 200L, 3 to 300L),
            items.sortedWith(ActiveFirstSort.comparator()),
        )
    }
}
