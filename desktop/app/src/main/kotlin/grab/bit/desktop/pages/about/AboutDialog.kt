package grab.bit.desktop.pages.about

import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.desktop.window.custom.WindowTitle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.window.custom.WindowIcon
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.resources.Res
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun ShowAboutDialog(appComponent: AppComponent) {
    if (appComponent.showAboutPage.collectAsState().value) {
        AboutDialog(
            onClose = {
                appComponent.closeAbout()
            },
            onRequestShowOpenSourceLibraries = {
                appComponent.openOpenSourceLibrariesPage()
            },
            onRequestShowTranslators = {
                appComponent.openTranslatorsPage()
            }
        )
    }
}

@Composable
fun AboutDialog(
    onClose: () -> Unit,
    onRequestShowOpenSourceLibraries: () -> Unit,
    onRequestShowTranslators: () -> Unit,
) {
    CustomWindow(
        resizable = false,
        onRequestToggleMaximize = null,
        alwaysOnTop = false,
        onRequestMinimize = null,
        state = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(
                    DpSize(600.dp, 310.dp)
                        .applyUiScale(LocalUiScale.current)
                ),
                positionProvider = WindowPositionProvider.CenteredOnScreen
            ),
        ),
        onCloseRequest = onClose
    ) {
        WindowTitle(myStringResource(Res.string.about))
        WindowIcon(MyIcons.info)
        AboutPage(
            onRequestShowOpenSourceLibraries = onRequestShowOpenSourceLibraries,
            onRequestShowTranslators = onRequestShowTranslators
        )
    }
}
