package grab.bit.desktop.pages.enterurl

import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pages.enterurl.BaseEnterNewURLComponent
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.IDownloadCredentials

class DesktopEnterNewURLComponent(
    ctx: ComponentContext,
    config: Config,
    downloaderInUiRegistry: DownloaderInUiRegistry,
    onCloseRequest: () -> Unit,
    onRequestFinished: (IDownloadCredentials) -> Unit,
) : BaseEnterNewURLComponent(
    ctx = ctx,
    config = config,
    downloaderInUiRegistry = downloaderInUiRegistry,
    onCloseRequest = onCloseRequest,
    onRequestFinished = onRequestFinished,
) {
    sealed interface Effects : BaseEnterNewURLComponent.Effects.PlatformEffects {
        data object BringToFront : Effects
    }

    data object Config : BaseEnterNewURLComponent.Config

    fun bringToFront() {
        sendEffect(Effects.BringToFront)
    }
}

