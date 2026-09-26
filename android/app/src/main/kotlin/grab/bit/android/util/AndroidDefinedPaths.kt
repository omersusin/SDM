package grab.bit.android.util

import grab.bit.shared.util.DefinedPaths
import okio.Path

class AndroidDefinedPaths(
    dataDir: Path,
) : DefinedPaths(
    dataDir = dataDir
) {
    val onboardingFile = pagesStateDir.resolve("onboarding.json")
    val homePageFile = pagesStateDir.resolve("home.json")
    val manualOrderFile = pagesStateDir.resolve("manual_order.json")
    val browserBookmarksFile = pagesStateDir.resolve("browser_bookmarks.json")
}
