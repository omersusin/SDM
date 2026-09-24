package grab.bit.desktop.pages.checksum

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.shared.pages.checksum.BaseFileChecksumComponent
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun FileChecksumWindow(
    component: AppComponent
) {
    component.openedFileChecksumDialog.collectAsState().value.child?.instance?.let {
        FileChecksumWindow(it)
    }
}

@Composable
fun FileChecksumWindow(
    component: DesktopFileChecksumComponent
) {
    val uiScale = LocalUiScale.current
    val state = rememberWindowState(
        initialBoundsProvider = WindowBoundsProvider(
            sizeProvider = WindowSizeProvider.Fixed(
                size = DpSize(900.dp, 400.dp).applyUiScale(uiScale)
            ),
            positionProvider = WindowPositionProvider.CenteredOnScreen
        )
    )
    CustomWindow(
        state = state,
        onCloseRequest = component::onRequestClose
    ) {
        HandleEffects(component) {
            when (it) {
                is BaseFileChecksumComponent.Effects.Platform -> {
                    when (it as DesktopFileChecksumComponent.Effects) {
                        DesktopFileChecksumComponent.Effects.BringToFront -> {
                            state.requestMinimized(false)
                            window.toFront()
                        }
                    }
                }
            }
        }
        FileChecksumPage(component)
    }
}
