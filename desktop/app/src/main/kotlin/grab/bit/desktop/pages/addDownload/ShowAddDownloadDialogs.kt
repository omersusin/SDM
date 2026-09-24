package grab.bit.desktop.pages.addDownload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.DesktopAddDownloadDialogManager
import grab.bit.desktop.pages.addDownload.multiple.DesktopAddMultiDownloadComponent
import grab.bit.desktop.pages.addDownload.multiple.AddMultiItemPage
import grab.bit.desktop.pages.addDownload.single.AddDownloadPage
import grab.bit.shared.pages.adddownload.single.BaseAddSingleDownloadComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.desktop.window.custom.WindowIcon
import grab.bit.desktop.window.custom.WindowTitle
import grab.bit.resources.Res
import grab.bit.shared.pages.adddownload.AddDownloadComponent
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.desktop.PlatformAppActivator
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun ShowAddDownloadDialogs(component: DesktopAddDownloadDialogManager) {
    val openedAddDownloadDialogs = component.openedAddDownloadDialogs.collectAsState().value
    for (addDownloadComponent in openedAddDownloadDialogs) {
        key(addDownloadComponent.id) {
            AddDownloadWindow(
                addDownloadComponent = addDownloadComponent,
                onRequestClose = {
                    component.closeAddDownloadDialog(addDownloadComponent.id)
                }
            )
        }
    }
}

@Composable
private fun AddDownloadWindow(
    addDownloadComponent: AddDownloadComponent,
    onRequestClose: () -> Unit,
) {
    val shouldShowWindow by addDownloadComponent.shouldShowWindow.collectAsState()
    if (!shouldShowWindow) return
    val uiScale = LocalUiScale.current
    when (addDownloadComponent) {
        is BaseAddSingleDownloadComponent -> {
            val h = 265.applyUiScale(uiScale)
            val w = 500.applyUiScale(uiScale)
            val size = remember {
                DpSize(
                    height = h.dp,
                    width = w.dp,
                )
            }

            val state = rememberWindowState(
                initialBoundsProvider = WindowBoundsProvider(
                    sizeProvider = WindowSizeProvider.Fixed(
                        size = size
                    ),
                    positionProvider = WindowPositionProvider.CenteredOnScreen
                )
            )
            CustomWindow(
                state = state,
                onCloseRequest = onRequestClose,
                alwaysOnTop = true,
                minSize = DpSize(w.dp, h.dp),
            ) {
                LaunchedEffect(Unit) {
                    PlatformAppActivator.active()
                }
//                    BringToFront()
                WindowTitle(myStringResource(Res.string.add_download))
                WindowIcon(MyIcons.appIcon)
                AddDownloadPage(addDownloadComponent)
            }
        }

        is DesktopAddMultiDownloadComponent -> {
            val h = 450
            val w = 800
            val state = rememberWindowState(
                initialBoundsProvider = WindowBoundsProvider(
                    sizeProvider = WindowSizeProvider.Fixed(
                        height = h.dp,
                        width = w.dp,
                    ),
                    positionProvider = WindowPositionProvider.CenteredOnScreen
                )
            )
            CustomWindow(
                state = state,
                onCloseRequest = onRequestClose,
                alwaysOnTop = true,
                minSize = DpSize(w.dp, h.dp),
            ) {
                LaunchedEffect(Unit) {
                    PlatformAppActivator.active()
                }
//                    BringToFront()
                WindowTitle(myStringResource(Res.string.add_download))
                WindowIcon(MyIcons.appIcon)
                AddMultiItemPage(addDownloadComponent)
            }
        }
    }
}

//it seems not affect at all
//@Composable
//private fun WindowScope.BringToFront() {
//    LaunchedEffect(Unit) {
//        window.toFront()
//        window.requestFocus()
//    }
//}
