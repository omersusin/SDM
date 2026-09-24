package grab.bit.desktop.pages.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.shared.pages.category.CategoryComponent
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.util.desktop.screen.applyUiScale

@Composable
fun ShowCategoryDialogs(dialogManager: DesktopCategoryDialogManager) {
    val dialogs by dialogManager.openedCategoryDialogs.collectAsState()
    for (d in dialogs) {
        CategoryDialog(d)
    }
}

@Composable
private fun CategoryDialog(
    component: CategoryComponent,
) {
    CustomWindow(
        onCloseRequest = {
            component.close()
        },
        alwaysOnTop = true,
        state = rememberWindowState(
            initialBoundsProvider = WindowBoundsProvider(
                sizeProvider = WindowSizeProvider.Fixed(
                    size = DpSize(350.dp, 400.dp).applyUiScale(LocalUiScale.current),
                ),
                positionProvider = WindowPositionProvider.CenteredOnScreen
            )
        )
    ) {
        NewCategory(component)
    }
}
