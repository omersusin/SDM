package ir.amirab.downloader.torrent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MagnetParserTest {
    private val hex = "a".repeat(40)

    @Test
    fun parsesFullMagnet() {
        val uri = "magnet:?xt=urn:btih:$hex&dn=Big+Buck+Bunny&tr=udp%3A%2F%2Ftracker.example%2Fannounce&tr=udp%3A%2F%2Ftracker2.example%2Fannounce"
        val link = MagnetParser.parse(uri)
        assertEquals(hex, link?.infoHash)
        assertEquals("Big Buck Bunny", link?.name)
        assertEquals(
            listOf("udp://tracker.example/announce", "udp://tracker2.example/announce"),
            link?.trackers,
        )
    }

    @Test
    fun acceptsBase32Hash() {
        val hash = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val link = MagnetParser.parse("magnet:?xt=urn:btih:$hash")
        assertEquals(hash.lowercase(), link?.infoHash)
        assertEquals(null, link?.name)
        assertEquals(emptyList(), link?.trackers)
    }

    @Test
    fun rejectsMissingXt() {
        assertNull(MagnetParser.parse("magnet:?dn=name&tr=udp%3A%2F%2Fx"))
    }

    @Test
    fun rejectsWrongScheme() {
        assertNull(MagnetParser.parse("https://example.com/?xt=urn:btih:$hex"))
    }

    @Test
    fun rejectsBadHash() {
        assertNull(MagnetParser.parse("magnet:?xt=urn:btih=notahash"))
        assertNull(MagnetParser.parse("magnet:?xt=urn:btih:xyz"))
    }
}
