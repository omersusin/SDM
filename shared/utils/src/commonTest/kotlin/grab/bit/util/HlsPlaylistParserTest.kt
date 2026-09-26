package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HlsPlaylistParserTest {
    @Test
    fun masterParsesVariants() {
        val result = HlsPlaylistParser.parse(
            """
            #EXTM3U
            #EXT-X-STREAM-INF:BANDWIDTH=1280000,RESOLUTION=640x360,CODECS="avc1.64001e,mp4a.40.2"
            low/index.m3u8
            #EXT-X-STREAM-INF:BANDWIDTH=2560000,RESOLUTION=1280x720
            mid/index.m3u8
            #EXT-X-STREAM-INF:BANDWIDTH=5120000
            hi/index.m3u8
            """.trimIndent()
        )
        assertIs<HlsPlaylist.Master>(result)
        assertEquals(3, result.playlist.variantCount)
        assertEquals("low/index.m3u8", result.playlist.variants[0].uri)
        assertEquals(1280000L, result.playlist.variants[0].bandwidth)
        assertEquals("640x360", result.playlist.variants[0].resolution)
        assertEquals("avc1.64001e,mp4a.40.2", result.playlist.variants[0].codecs)
        assertEquals(2560000L, result.playlist.variants[1].bandwidth)
        assertNull(result.playlist.variants[2].resolution)
        assertNull(result.playlist.variants[2].codecs)
    }

    @Test
    fun masterSkipsDanglingStreamInf() {
        val result = HlsPlaylistParser.parseMaster(
            "#EXTM3U\n#EXT-X-STREAM-INF:BANDWIDTH=100\nok/index.m3u8\n#EXT-X-STREAM-INF:BANDWIDTH=200"
        )
        assertEquals(1, result.variantCount)
        assertEquals("ok/index.m3u8", result.variants[0].uri)
    }

    @Test
    fun mediaParsesSegmentsWithDurations() {
        val result = HlsPlaylistParser.parse(
            """
            #EXTM3U
            #EXT-X-TARGETDURATION:8
            #EXTINF:6.0,
            seg-0.ts
            #EXTINF:7.5,
            seg-1.ts
            #EXT-X-ENDLIST
            """.trimIndent()
        )
        assertIs<HlsPlaylist.Media>(result)
        assertEquals(2, result.playlist.segmentCount)
        assertEquals(6000L, result.playlist.segments[0].durationMs)
        assertEquals(7500L, result.playlist.segments[1].durationMs)
        assertEquals(13500L, result.playlist.totalDurationMs)
        assertEquals(8000L, result.playlist.targetDurationMs)
        assertTrue(result.playlist.endList)
    }

    @Test
    fun mediaTracksKeyMapAndClearsOnNone() {
        val result = HlsPlaylistParser.parseMedia(
            """
            #EXTM3U
            #EXT-X-KEY:METHOD=AES-128,URI="https://cdn.example.com/key.bin",IV=0x00000000000000000000000000000001
            #EXT-X-MAP:URI="init.mp4"
            #EXTINF:6.0,
            seg-0.m4s
            #EXTINF:6.0,
            seg-1.m4s
            #EXT-X-KEY:METHOD=NONE
            #EXTINF:6.0,
            seg-2.m4s
            """.trimIndent()
        )
        assertEquals(3, result.segmentCount)
        val first = result.segments[0]
        assertEquals("AES-128", first.key?.method)
        assertEquals("https://cdn.example.com/key.bin", first.key?.uri)
        assertEquals("0x00000000000000000000000000000001", first.key?.iv)
        assertEquals("init.mp4", first.mapUri)
        assertEquals("init.mp4", result.segments[1].mapUri)
        assertNull(result.segments[2].key)
    }

    @Test
    fun strayUriWithoutExtinfIsIgnored() {
        val result = HlsPlaylistParser.parseMedia(
            "#EXTM3U\nstray.ts\n#EXTINF:4.0,\nseg-0.ts"
        )
        assertEquals(1, result.segmentCount)
        assertEquals("seg-0.ts", result.segments[0].uri)
    }

    @Test
    fun missingExtm3uIsInvalid() {
        assertEquals(HlsPlaylist.Invalid, HlsPlaylistParser.parse("#EXTINF:4.0,\nseg-0.ts"))
        assertEquals(HlsPlaylist.Invalid, HlsPlaylistParser.parse(""))
        assertEquals(HlsPlaylist.Invalid, HlsPlaylistParser.parse("  "))
    }
}
