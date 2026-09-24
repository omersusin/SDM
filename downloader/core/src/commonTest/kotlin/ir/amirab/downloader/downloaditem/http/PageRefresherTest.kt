package ir.amirab.downloader.downloaditem.http

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PageRefresherTest {
    @Test
    fun findsFreshUrlByFileName() = runBlocking {
        val html = """
            <html><body>
            <video src="https://cdn.example/v9/clip.mp4?token=fresh"></video>
            </body></html>
        """.trimIndent()
        val fresh = PageRefresher.refresh(
            RefreshRequest(
                link = "https://cdn.example/v1/clip.mp4?token=stale",
                page = "https://site.example/watch",
            )
        ) { html }
        assertEquals("https://cdn.example/v9/clip.mp4?token=fresh", fresh)
    }

    @Test
    fun noPageOrNoMatchGivesNull() = runBlocking {
        assertNull(
            PageRefresher.refresh(RefreshRequest("https://cdn.example/a.mp4", null)) { "" }
        )
        assertNull(
            PageRefresher.refresh(
                RefreshRequest("https://cdn.example/a.mp4", "https://site.example/w"),
            ) { "<html></html>" }
        )
    }
}
