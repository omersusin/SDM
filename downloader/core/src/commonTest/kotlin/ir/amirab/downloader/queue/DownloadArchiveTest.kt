package ir.amirab.downloader.queue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloadArchiveTest {
    @Test
    fun addAndContains() {
        val archive = DownloadArchive()
        assertFalse(archive.contains("https://cdn.example/a.mp4"))
        archive.add("https://cdn.example/a.mp4", 1000L)
        assertTrue(archive.contains("https://cdn.example/a.mp4"))
    }

    @Test
    fun fragmentIgnored() {
        val archive = DownloadArchive()
        archive.add("https://cdn.example/a.mp4#t=10", 1000L)
        assertTrue(archive.contains("https://cdn.example/a.mp4"))
    }

    @Test
    fun pruneOld() {
        val archive = DownloadArchive()
        archive.add("https://cdn.example/a.mp4", 1000L)
        archive.add("https://cdn.example/b.mp4", 5000L)
        assertEquals(1, archive.pruneOlderThan(2000L))
        assertFalse(archive.contains("https://cdn.example/a.mp4"))
        assertTrue(archive.contains("https://cdn.example/b.mp4"))
    }

    @Test
    fun removeAndClear() {
        val archive = DownloadArchive()
        archive.add("https://cdn.example/a.mp4", 1000L)
        archive.remove("https://cdn.example/a.mp4")
        assertFalse(archive.contains("https://cdn.example/a.mp4"))
        archive.add("https://cdn.example/b.mp4", 1000L)
        archive.clear()
        assertEquals(0, archive.size)
    }
}
