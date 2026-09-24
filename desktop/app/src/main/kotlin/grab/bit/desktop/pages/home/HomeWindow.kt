package grab.bit.desktop.pages.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.utils.AppInfo
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.desktop.window.custom.isMinimizedOrNull
import grab.bit.desktop.window.custom.placementOrNull
import grab.bit.desktop.window.custom.rememberWindowController
import grab.bit.desktop.window.custom.sizeOrNull
import grab.bit.shared.util.LocalShortCutManager
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild
import grab.bit.shared.util.ui.icon.MyIcons
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeWindow(
    appComponent: AppComponent,
) {
    appComponent.showHomeSlot.rememberChild()?.let {
        HomeWindow(it, appComponent::closeHome)
    }
}

@Composable
private fun HomeWindow(
    homeComponent: HomeComponent,
    onCLoseRequest: () -> Unit,
) {
    val size by homeComponent.windowSize.collectAsState()
    val isMaximized by homeComponent.isMaximized.collectAsState()
    val windowState = rememberWindowState(
        initialBoundsProvider = WindowBoundsProvider(
            sizeProvider = WindowSizeProvider.Fixed(
                size = size,
            ),
            positionProvider = WindowPositionProvider.CenteredOnScreen
        ),
        initialPlacement = if (isMaximized) {
            WindowPlacement.Maximized
        } else {
            WindowPlacement.Floating
        }
    )
    val windowIcon = MyIcons.appIcon
    val windowController = rememberWindowController(
        AppInfo.displayName,
        windowIcon.rememberPainter(),
    )

    CompositionLocalProvider(
        LocalShortCutManager provides homeComponent.shortcutManager
    ) {
        CustomWindow(
            state = windowState,
            onCloseRequest = onCLoseRequest,
            windowController = windowController,
            onKeyEvent = {
                homeComponent.shortcutManager.handle(it)
            },
            minSize = DpSize(400.dp, 400.dp),
        ) {
            LaunchedEffect(windowState.sizeOrNull) {
                if (windowState.isMinimizedOrNull == false && windowState.placementOrNull == WindowPlacement.Floating) {
                    windowState.sizeOrNull?.let {
                        homeComponent.setWindowSize(it)
                    }
                }
            }
            LaunchedEffect(windowState) {
                snapshotFlow { windowState.placementOrNull }
                    .filterNotNull()
                    .onEach {
                        homeComponent.setIsMaximized(it == WindowPlacement.Maximized)
                    }.launchIn(this)
            }
            HandleEffects(homeComponent) {
                when (it) {
                    HomeComponent.Effects.BringToFront -> {
                        windowState.requestMinimized(false)
                        window.toFront()
                    }

                    else -> {}
                }
            }
            BoxWithConstraints {
                HomePage(homeComponent)
            }
        }
    }
}
