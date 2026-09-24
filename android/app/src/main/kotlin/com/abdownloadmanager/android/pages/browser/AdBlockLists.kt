package com.abdownloadmanager.android.pages.browser

import android.content.Context
import ir.amirab.util.AdBlockMatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

// Downloads EasyList once, caches it, feeds the intercept matcher.
// ponytail: plain URL.readText, no new deps; matcher only reads ||domain^.
object AdBlockLists {
    const val EASYLIST_URL = "https://easylist.to/easylist/easylist.txt"
    private const val CACHE_NAME = "adblock_easylist.txt"
    private const val MAX_AGE_MILLIS = 7L * 24 * 60 * 60 * 1000

    suspend fun ensureLoaded(context: Context, interceptor: DownloadInterceptor) {
        if (interceptor.adBlock != null) return
        withContext(Dispatchers.IO) {
            runCatching {
                val file = File(context.filesDir, CACHE_NAME)
                val lines = if (file.exists() && System.currentTimeMillis() - file.lastModified() < MAX_AGE_MILLIS) {
                    file.readLines()
                } else {
                    val fresh = URL(EASYLIST_URL).readText().lines()
                    file.writeText(fresh.joinToString("\n"))
                    fresh
                }
                val matcher = AdBlockMatcher.parse(lines)
                if (matcher.ruleCount > 0) {
                    interceptor.adBlock = matcher
                }
            }
        }
    }
}
