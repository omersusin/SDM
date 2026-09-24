package ir.amirab.downloader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpeedProfileTest {
    @Test
    fun highIsUnlimited() {
        assertEquals(0L, SpeedProfile.HIGH.bytesPerSec)
    }

    @Test
    fun orderingAndValues() {
        assertTrue(SpeedProfile.SNAIL.bytesPerSec < SpeedProfile.LOW.bytesPerSec)
        assertEquals(512L * 1024, SpeedProfile.LOW.bytesPerSec)
        assertEquals(64L * 1024, SpeedProfile.SNAIL.bytesPerSec)
    }

    @Test
    fun namesAreStableForStoredSettings() {
        assertEquals(SpeedProfile.HIGH, enumValueOf("HIGH"))
        assertEquals(SpeedProfile.LOW, enumValueOf("LOW"))
        assertEquals(SpeedProfile.SNAIL, enumValueOf("SNAIL"))
    }
}
