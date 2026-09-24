package grab.bit.downloader.downloaditem.http

import grab.bit.util.PageMediaHarvester

data class RefreshRequest(
    val link: String,
    val page: String?,
)

// Re-resolves an expired URL from its source page (1DM/ADM recipe): refetch
// the page, harvest media, match by file name. Fetch is injected so tests
// use a fake; the job supplies real HTTP.
object PageRefresher {
    suspend fun refresh(
        req: RefreshRequest,
        fetchHtml: suspend (String) -> String?,
    ): String? {
        val page = req.page ?: return null
        val html = runCatching { fetchHtml(page) }.getOrNull() ?: return null
        val fileName = req.link.substringAfterLast('/').substringBefore('?')
        if (fileName.isBlank()) return null
        return PageMediaHarvester.harvest(html, page).firstOrNull {
            it.substringAfterLast('/').substringBefore('?') == fileName
        }
    }
}
