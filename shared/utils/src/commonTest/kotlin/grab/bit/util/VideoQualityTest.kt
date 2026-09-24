package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VideoQualityTest {
    @Test
    fun defaultIsAuto() {
        assertEquals(VideoQuality.AUTO, VideoQualities.default)
    }

    @Test
    fun heights() {
        assertNull(VideoQualities.maxHeightOf(VideoQuality.AUTO))
        assertNull(VideoQualities.maxHeightOf(VideoQuality.HIGHEST))
        assertEquals(1080, VideoQualities.maxHeightOf(VideoQuality.P1080))
        assertEquals(360, VideoQualities.maxHeightOf(VideoQuality.P360))
    }

    @Test
    fun migration() {
        // 720 was the old default: indistinguishable from fresh → AUTO.
        assertEquals(VideoQuality.AUTO, VideoQualities.migrateStoredHeight(720))
        assertEquals(VideoQuality.P1080, VideoQualities.migrateStoredHeight(1080))
        assertEquals(VideoQuality.P480, VideoQualities.migrateStoredHeight(480))
        assertEquals(VideoQuality.P360, VideoQualities.migrateStoredHeight(360))
        assertEquals(VideoQuality.HIGHEST, VideoQualities.migrateStoredHeight(900))
        assertEquals(VideoQuality.HIGHEST, VideoQualities.migrateStoredHeight(4320))
    }

    @Test
    fun namesAreStableForStoredSettings() {
        assertEquals(VideoQuality.AUTO, enumValueOf("AUTO"))
        assertEquals(VideoQuality.P720, enumValueOf("P720"))
    }
}
