package ir.amirab.downloader.downloaditem.http

import ir.amirab.downloader.exception.UnSuccessfulResponseException

// Decides when a failed HTTP download needs its link refreshed from the
// source page instead of a plain retry. 403/410 mean the URL itself expired;
// anything else keeps the normal retry path. Re-resolution happens later.
object LinkRefreshPolicy {
    fun needsRefresh(e: Throwable): Boolean {
        val code = (e as? UnSuccessfulResponseException)?.code
        return code == 403 || code == 410
    }
}
