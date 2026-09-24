package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BulkPatternTest {
    @Test
    fun expandsZeroPadded() {
        val urls = BulkPattern.expand("https://cdn.example/file[001-003].zip")
        assertEquals(
            listOf(
                "https://cdn.example/file001.zip",
                "https://cdn.example/file002.zip",
                "https://cdn.example/file003.zip",
            ),
            urls,
        )
    }

    @Test
    fun noPatternGivesNull() {
        assertNull(BulkPattern.expand("https://cdn.example/file.zip"))
        assertFalse(BulkPattern.hasPattern("https://cdn.example/file.zip"))
        assertTrue(BulkPattern.hasPattern("https://cdn.example/file[1-3].zip"))
    }

    @Test
    fun rejectsReversedAndHuge() {
        assertNull(BulkPattern.expand("https://cdn.example/file[5-2].zip"))
        assertNull(BulkPattern.expand("https://cdn.example/file[1-99999999].zip"))
    }
}
