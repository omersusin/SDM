package ir.amirab.downloader.downloaditem.http

import ir.amirab.downloader.exception.UnSuccessfulResponseException
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkRefreshPolicyTest {
    @Test
    fun expiredCodesNeedRefresh() {
        assertTrue(LinkRefreshPolicy.needsRefresh(UnSuccessfulResponseException(403, "Forbidden")))
        assertTrue(LinkRefreshPolicy.needsRefresh(UnSuccessfulResponseException(410, "Gone")))
    }

    @Test
    fun otherFailuresKeepRetryPath() {
        assertFalse(LinkRefreshPolicy.needsRefresh(UnSuccessfulResponseException(429, "Slow down")))
        assertFalse(LinkRefreshPolicy.needsRefresh(UnSuccessfulResponseException(500, "Error")))
        assertFalse(LinkRefreshPolicy.needsRefresh(UnSuccessfulResponseException(404, "Missing")))
        assertFalse(LinkRefreshPolicy.needsRefresh(IOException("socket closed")))
    }
}
