package grab.bit.desktop.pages.batchdownload

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.shared.pages.batchdownload.BaseBatchDownloadComponent
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun BatchDownloadWindow(appComponent: AppComponent) {
    appComponent.batchDownloadSlot.rememberChild()?.let {
        BatchDownloadWindow(it)
    }
}

@Composable
private fun BatchDownloadWindow(desktopBatchDownloadComponent: DesktopBatchDownloadComponent) {
    CustomWindow(
        state = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(
                    size = DpSize(500.dp, 420.dp)
                        .applyUiScale(LocalUiScale.current),
                ),
                positionProvider = WindowPositionProvider.CenteredOnScreen
            )
        ),
        onCloseRequest = desktopBatchDownloadComponent.onClose
    ) {
        HandleEffects(desktopBatchDownloadComponent) {
            when (it) {
                DesktopBatchDownloadComponent.Effects.BringToFront -> window.toFront()
                is BaseBatchDownloadComponent.Effects.PlatformEffects -> {
                    //
                }
            }
        }
        BatchDownload(desktopBatchDownloadComponent)
    }
}
