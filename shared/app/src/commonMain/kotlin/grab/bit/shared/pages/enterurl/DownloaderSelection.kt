package grab.bit.shared.pages.enterurl

import grab.bit.shared.downloaderinui.TADownloaderInUI

sealed interface DownloaderSelection {
    data object Auto : DownloaderSelection
    data class Fixed(
        val downloaderInUi: TADownloaderInUI,
    ) : DownloaderSelection
}
