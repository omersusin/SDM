package grab.bit.downloader.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RetryAfterTest {
    @Test
    fun delaySeconds() {
        assertEquals(5000L, RetryAfter.parseMillis("5", 0L))
        assertEquals(0L, RetryAfter.parseMillis("0", 0L))
    }

    @Test
    fun httpDate() {
        // Sun, 06 Nov 1994 08:49:37 GMT == 784111777000
        assertEquals(784111777000L, RetryAfter.parseHttpDate("Sun, 06 Nov 1994 08:49:37 GMT"))
        assertEquals(37_000L, RetryAfter.parseMillis("Sun, 06 Nov 1994 08:50:14 GMT", 784111777000L))
        assertEquals(0L, RetryAfter.parseMillis("Sun, 06 Nov 1994 08:49:37 GMT", 784111777999L))
    }

    @Test
    fun garbageGivesNull() {
        assertNull(RetryAfter.parseMillis(null, 0L))
        assertNull(RetryAfter.parseMillis("", 0L))
        assertNull(RetryAfter.parseMillis("-3", 0L))
        assertNull(RetryAfter.parseMillis("tomorrow", 0L))
    }
}
