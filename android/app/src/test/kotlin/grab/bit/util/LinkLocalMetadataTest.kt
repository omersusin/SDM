package grab.bit.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LinkLocalMetadataTest {
    @Test
    fun metadataIpBlocked() {
        assertTrue(HttpUrlUtils.isLinkLocalMetadataHost("169.254.169.254"))
        assertTrue(HttpUrlUtils.isLinkLocalMetadataHost("169.254.0.1"))
    }

    @Test
    fun googleMetadataHostBlocked() {
        assertTrue(HttpUrlUtils.isLinkLocalMetadataHost("metadata.google.internal"))
        assertTrue(HttpUrlUtils.isLinkLocalMetadataHost("foo.metadata.google.internal"))
    }

    @Test
    fun lanAndPublicAllowed() {
        assertFalse(HttpUrlUtils.isLinkLocalMetadataHost("192.168.1.10"))
        assertFalse(HttpUrlUtils.isLinkLocalMetadataHost("10.0.0.5"))
        assertFalse(HttpUrlUtils.isLinkLocalMetadataHost("172.16.0.9"))
        assertFalse(HttpUrlUtils.isLinkLocalMetadataHost("8.8.8.8"))
        assertFalse(HttpUrlUtils.isLinkLocalMetadataHost("example.com"))
    }

    @Test
    fun isValidUrlRejectsMetadata() {
        assertFalse(HttpUrlUtils.isValidUrl("http://169.254.169.254/latest/meta-data/"))
        assertTrue(HttpUrlUtils.isValidUrl("http://192.168.1.1/file.zip"))
        assertTrue(HttpUrlUtils.isValidUrl("https://example.com/file.zip"))
    }
}
