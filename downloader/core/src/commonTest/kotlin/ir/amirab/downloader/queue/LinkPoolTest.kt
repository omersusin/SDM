package ir.amirab.downloader.queue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinkPoolTest {
    private fun link(url: String) = CapturedLink(url, "https://page.example", "$url-name")

    @Test
    fun deduplicatesAndSelectsNewByDefault() {
        val pool = LinkPool()
        pool.add(link("https://cdn.example/a.mp4"))
        pool.add(link("https://cdn.example/a.mp4"))
        pool.add(link("https://cdn.example/b.mp4"))
        assertEquals(2, pool.size)
        assertEquals(2, pool.selectedCount)
    }

    @Test
    fun toggleAndRemoveSelected() {
        val pool = LinkPool()
        pool.addAll(listOf(link("https://cdn.example/a.mp4"), link("https://cdn.example/b.mp4")))
        pool.toggleSelect("https://cdn.example/a.mp4")
        assertEquals(listOf("https://cdn.example/b.mp4"), pool.selectedLinks().map { it.url })
        val removed = pool.removeSelected()
        assertEquals(listOf("https://cdn.example/b.mp4"), removed.map { it.url })
        assertEquals(1, pool.size)
        assertEquals(0, pool.selectedCount)
    }

    @Test
    fun toggleUnknownIsNoop() {
        val pool = LinkPool()
        pool.toggleSelect("https://cdn.example/none.mp4")
        assertTrue(pool.selectedLinks().isEmpty())
    }

    @Test
    fun clearResets() {
        val pool = LinkPool()
        pool.add(link("https://cdn.example/a.mp4"))
        pool.clear()
        assertEquals(0, pool.size)
        assertEquals(0, pool.selectedCount)
    }

    @Test
    fun removeUrlsDropsSubset() {
        val pool = LinkPool()
        pool.addAll(listOf(link("https://cdn.example/a.mp4"), link("https://cdn.example/b.mp4")))
        pool.removeUrls(listOf("https://cdn.example/a.mp4"))
        assertEquals(1, pool.size)
        assertEquals(1, pool.selectedCount)
    }
}
