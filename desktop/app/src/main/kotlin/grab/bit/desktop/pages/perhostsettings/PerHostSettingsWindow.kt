package grab.bit.desktop.pages.perhostsettings

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.shared.pages.perhostsettings.BasePerHostSettingsComponent
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun PerHostSettingsWindow(
    appComponent: AppComponent
) {
    val component = appComponent.perHostSettingsSlot.rememberChild()
    if (component != null) {
        val windowState = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(
                    DpSize(
                        600.dp,
                        400.dp,
                    )
                        .applyUiScale(LocalUiScale.current)
                ),
                positionProvider = WindowPositionProvider.CenteredOnScreen
            )
        )
        CustomWindow(
            state = windowState,
            onCloseRequest = appComponent::closePerHostSettings,
        ) {
            HandleEffects(component) {
                when (it) {
                    is BasePerHostSettingsComponent.Effects.Platform -> {
                        when (it as DesktopPerHostSettingsComponent.Effects) {
                            DesktopPerHostSettingsComponent.Effects.BringToFront -> {
                                windowState.requestMinimized(false)
                                window.toFront()
                            }
                        }
                    }
                }
            }
            PerHostSettingsPage(component)
        }
    }
}
