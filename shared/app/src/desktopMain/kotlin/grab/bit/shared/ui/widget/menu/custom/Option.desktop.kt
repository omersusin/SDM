package grab.bit.shared.ui.widget.menu.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberCursorPositionProvider
import grab.bit.util.compose.action.MenuItem

@Composable
actual fun ShowOptionsInPopup(
    menu: MenuItem.SubMenu,
    onDismissRequest: () -> Unit
) {
    Popup(
        popupPositionProvider = rememberCursorPositionProvider(
            alignment = Alignment.BottomEnd
        ),
        onDismissRequest = onDismissRequest
    ) {
        RenderOptions(menu, onDismissRequest)
    }
}
