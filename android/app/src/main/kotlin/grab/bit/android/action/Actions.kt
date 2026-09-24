package grab.bit.android.action

import grab.bit.android.util.pagemanager.IBrowserPageManager
import grab.bit.resources.Res
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.util.compose.action.AnAction
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource

fun createOpenBrowserAction(
    browserPageManager: IBrowserPageManager,
): AnAction {
    return simpleAction(
        Res.string.browser.asStringSource(),
        MyIcons.earth,
    ) {
        browserPageManager.openBrowser(null)
    }
}
