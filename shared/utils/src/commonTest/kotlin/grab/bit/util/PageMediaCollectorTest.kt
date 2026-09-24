package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PageMediaCollectorTest {
    @Test
    fun collectsDirectAndStreamUrls() {
        val collector = PageMediaCollector()
        collector.observe("https://example.com/page.html")
        collector.observe("https://cdn.example.com/a.mp4")
        collector.observe("https://cdn.example.com/live/index.m3u8")
        val snapshot = collector.snapshot()
        assertEquals(2, snapshot.size)
        assertIs<MediaCandidate.Direct>(snapshot[0])
        assertIs<MediaCandidate.Stream>(snapshot[1])
    }

    @Test
    fun deduplicatesSameUrl() {
        val collector = PageMediaCollector()
        repeat(3) { collector.observe("https://cdn.example.com/a.mp4") }
        assertEquals(1, collector.count)
    }

    @Test
    fun ignoresNonMedia() {
        val collector = PageMediaCollector()
        collector.observe("https://example.com/app.js")
        collector.observe("https://example.com/style.css")
        collector.observe("")
        assertTrue(collector.snapshot().isEmpty())
    }

    @Test
    fun clearResetsForNextPage() {
        val collector = PageMediaCollector()
        collector.observe("https://cdn.example.com/a.mp4")
        collector.clear()
        assertEquals(0, collector.count)
        collector.observe("https://cdn.example.com/b.mp3")
        assertEquals(1, collector.count)
    }
}
