package ir.amirab.downloader.video

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VideoFormatPickerTest {
    private fun format(
        id: String,
        vcodec: String? = "avc1",
        acodec: String? = "mp4a",
        height: Int? = 720,
    ) = VideoFormat(id, "mp4", vcodec, acodec, 1280, height, 1_000_000, "https://cdn.example/$id")

    @Test
    fun splitsAudioAndVideo() {
        val formats = listOf(
            format("a", vcodec = "none", acodec = "mp4a"),
            format("v", vcodec = "avc1", acodec = "none"),
            format("b", vcodec = "avc1", acodec = "mp4a"),
        )
        assertEquals(listOf("a"), VideoFormatPicker.audioOnly(formats).map { it.id })
        assertEquals(listOf("v", "b"), VideoFormatPicker.videoFormats(formats).map { it.id })
    }

    @Test
    fun joinsIdsWithPlus() {
        assertEquals("v+a", VideoFormatPicker.formatIdString(listOf("v", "a")))
    }

    @Test
    fun picksBestAtOrBelowHeight() {
        val formats = listOf(
            format("360", height = 360),
            format("720", height = 720),
            format("1080", height = 1080),
        )
        assertEquals("720", VideoFormatPicker.bestForHeight(formats, 720)?.id)
    }

    @Test
    fun fallsBackToLowestAboveHeight() {
        val formats = listOf(format("720", height = 720), format("1080", height = 1080))
        assertEquals("720", VideoFormatPicker.bestForHeight(formats, 360)?.id)
    }

    @Test
    fun emptyGivesNull() {
        assertNull(VideoFormatPicker.bestForHeight(emptyList(), 720))
    }

    @Test
    fun sortsBestFirst() {
        val formats = listOf(
            format("audio", vcodec = "none", acodec = "mp4a", height = null),
            format("low", height = 360),
            format("high", height = 1080),
            format("mid", height = 720),
        )
        assertEquals(
            listOf("high", "mid", "low", "audio"),
            VideoFormatPicker.sortBestFirst(formats).map { it.id },
        )
    }

    @Test
    fun bitrateBreaksTies() {
        val a = format("a", height = 720).copy(tbr = 1000.0)
        val b = format("b", height = 720).copy(tbr = 2000.0)
        assertEquals(listOf("b", "a"), VideoFormatPicker.sortBestFirst(listOf(a, b)).map { it.id })
    }
}
