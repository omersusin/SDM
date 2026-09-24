package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MediaSnifferTest {
    @Test
    fun directMp4ByExtension() {
        val result = MediaSniffer.sniff("https://cdn.example.com/videos/clip.mp4")
        assertIs<MediaCandidate.Direct>(result)
        assertEquals("clip.mp4", result.fileName)
    }

    @Test
    fun extensionIsCaseInsensitiveAndIgnoresQuery() {
        val result = MediaSniffer.sniff("https://cdn.example.com/v/EP12.MP4?token=abc")
        assertIs<MediaCandidate.Direct>(result)
        assertEquals("EP12.MP4", result.fileName)
    }

    @Test
    fun hlsPlaylistByExtension() {
        val result = MediaSniffer.sniff("https://cdn.example.com/live/index.m3u8?key=1")
        assertEquals(MediaCandidate.Stream("https://cdn.example.com/live/index.m3u8?key=1", StreamKind.HLS), result)
    }

    @Test
    fun dashManifestByExtension() {
        val result = MediaSniffer.sniff("https://cdn.example.com/vod/manifest.mpd")
        assertEquals(MediaCandidate.Stream("https://cdn.example.com/vod/manifest.mpd", StreamKind.DASH), result)
    }

    @Test
    fun directByMimeWhenUrlHasNoExtension() {
        val result = MediaSniffer.sniff("https://example.com/watch?id=42", "video/mp4")
        assertIs<MediaCandidate.Direct>(result)
    }

    @Test
    fun mimeWithCharsetParamStillMatches() {
        val result = MediaSniffer.sniff("https://example.com/stream/7", "application/x-mpegURL; charset=utf-8")
        assertEquals(MediaCandidate.Stream("https://example.com/stream/7", StreamKind.HLS), result)
    }

    @Test
    fun htmlPageIsNotMedia() {
        assertEquals(MediaCandidate.NotMedia, MediaSniffer.sniff("https://example.com/news/today.html", "text/html"))
    }

    @Test
    fun pageAssetsAreNotMedia() {
        assertEquals(MediaCandidate.NotMedia, MediaSniffer.sniff("https://example.com/app.js"))
        assertEquals(MediaCandidate.NotMedia, MediaSniffer.sniff("https://example.com/style.css"))
        assertEquals(MediaCandidate.NotMedia, MediaSniffer.sniff("https://example.com/logo.png"))
    }

    @Test
    fun blankUrlIsNotMedia() {
        assertEquals(MediaCandidate.NotMedia, MediaSniffer.sniff("  "))
    }
}
