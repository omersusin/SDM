package ir.amirab.util

import kotlin.test.Test
import kotlin.test.assertEquals

class PageMediaHarvesterTest {
    private val page = "https://site.example/gallery/index.html"
    private val html = """
        <html><body>
        <img src="https://cdn.example/a.jpg">
        <img src="/b.png">
        <img src="//cdn.example/c.gif">
        <img src="d.webp">
        <video src="https://cdn.example/e.mp4"></video>
        <img src="https://cdn.example/a.jpg">
        <img src="data:image/png;base64,xx">
        <a href="https://cdn.example/f.jpg">link</a>
        </body></html>
    """.trimIndent()

    @Test
    fun harvestsAndResolves() {
        assertEquals(
            listOf(
                "https://cdn.example/a.jpg",
                "https://site.example/b.png",
                "https://cdn.example/c.gif",
                "https://site.example/gallery/d.webp",
                "https://cdn.example/e.mp4",
            ),
            PageMediaHarvester.harvest(html, page),
        )
    }

    @Test
    fun badPageGivesEmpty() {
        assertEquals(emptyList(), PageMediaHarvester.harvest(html, "not a url"))
    }
}
