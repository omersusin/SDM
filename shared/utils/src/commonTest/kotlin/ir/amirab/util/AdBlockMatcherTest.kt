package ir.amirab.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdBlockMatcherTest {
    private val matcher = AdBlockMatcher.parse(
        listOf(
            "! comment line",
            "@@||allow.example^",
            "||ads.example^",
            "||tracker.example^\$third-party",
            "/regex-rule/",
            "||wildcard*.example^",
            "",
        )
    )

    @Test
    fun blocksListedDomainAndSubdomains() {
        assertTrue(matcher.isBlocked("https://ads.example/banner.js"))
        assertTrue(matcher.isBlocked("https://sub.ads.example/img.png"))
    }

    @Test
    fun doesNotBlockUnrelatedOrLookalikes() {
        assertFalse(matcher.isBlocked("https://example.com/"))
        assertFalse(matcher.isBlocked("https://notads.example/"))
        assertFalse(matcher.isBlocked("https://myads.example.evil.com/"))
    }

    @Test
    fun ignoresNonDomainRules() {
        // only ads.example + tracker.example parsed
        assertTrue(matcher.isBlocked("https://tracker.example/pixel"))
        assertFalse(matcher.isBlocked("https://wildcard0.example/x"))
    }

    @Test
    fun invalidUrlIsNotBlocked() {
        assertFalse(matcher.isBlocked("not a url"))
    }
}
