package ir.amirab.util

import kotlin.test.Test
import kotlin.test.assertEquals

class GrabberUiModeTest {
    @Test
    fun defaultIsMenuBadge() {
        assertEquals(GrabberUiMode.MENU_BADGE, GrabberUiModes.default)
    }

    @Test
    fun namesAreStableForStoredSettings() {
        // schemakt persists enum by name; renames break existing installs.
        assertEquals(GrabberUiMode.MENU_BADGE, enumValueOf("MENU_BADGE"))
        assertEquals(GrabberUiMode.AUTO_POPUP, enumValueOf("AUTO_POPUP"))
    }
}
