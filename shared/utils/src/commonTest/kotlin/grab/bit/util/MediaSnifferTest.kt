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

    @Test
    fun m4sSegmentByExtensionWithQueryAndFragment() {
        val result = MediaSniffer.sniff("https://cdn.example.com/vod/seg-12.m4s?token=abc#frag")
        assertIs<MediaCandidate.Direct>(result)
        assertEquals("seg-12.m4s", result.fileName)
    }

    @Test
    fun commonAudioVideoExtensionsAreCaseInsensitive() {
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/a/TRACK.OPUS"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/v/clip.WEBM?x=1"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/v/clip.MKV"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/a/note.FLAC"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/a/note.WAV"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/a/song.M4A"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/v/clip.MOV"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/v/clip.AVI"))
        assertIs<MediaCandidate.Direct>(MediaSniffer.sniff("https://cdn.example.com/v/seg.TS?k=2"))
    }

    @Test
    fun applicationMp4MimeWhenUrlHasNoExtension() {
        val result = MediaSniffer.sniff("https://example.com/dl/file?id=7", "application/mp4")
        assertIs<MediaCandidate.Direct>(result)
    }

    @Test
    fun audioMimeWhenUrlHasNoExtension() {
        val result = MediaSniffer.sniff("https://example.com/stream/9", "audio/webm")
        assertIs<MediaCandidate.Direct>(result)
    }
}
