package grab.bit.downloader.part

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StragglerPickerTest {
    @Test
    fun picksSlowestEtaOverLargestFastPart() {
        val candidates = listOf(
            StragglerPicker.Candidate(id = 1, remaining = 10_000_000, bytesPerSec = 5_000_000.0),
            StragglerPicker.Candidate(id = 2, remaining = 4_000_000, bytesPerSec = 100_000.0),
        )
        assertEquals(2L, StragglerPicker.pick(candidates)?.id)
    }

    @Test
    fun stalledPartWins() {
        val candidates = listOf(
            StragglerPicker.Candidate(id = 1, remaining = 8_000_000, bytesPerSec = 1_000_000.0),
            StragglerPicker.Candidate(id = 2, remaining = 8_000_000, bytesPerSec = 0.0),
        )
        assertEquals(2L, StragglerPicker.pick(candidates)?.id)
    }

    @Test
    fun ignoresPartsBelowMinRemaining() {
        val candidates = listOf(
            StragglerPicker.Candidate(id = 1, remaining = 1000, bytesPerSec = 1.0),
        )
        assertNull(StragglerPicker.pick(candidates))
    }

    @Test
    fun emptyGivesNull() {
        assertNull(StragglerPicker.pick(emptyList()))
    }
}
