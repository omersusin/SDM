package grab.bit.android.ui.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitle
import grab.bit.android.ui.SheetUI
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.TransparentIconActionButton
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.ResponsiveDialogScope
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.asStringSource

@Composable
private fun ResponsiveDialogScope.RenderMenuInSheetUi(
    menuStack: StackMenuState,
    onDismissRequest: () -> Unit,
) {
    val currentMenu = menuStack.currentMenu
    SheetUI(
        header = {
            SheetHeader(
                headerTitle = {
                    SheetTitle(
                        title = currentMenu.title.collectAsState().value.rememberString(),
                        icon = currentMenu.icon.collectAsState().value,
                    )
                },
                headerActions = {
                    if (menuStack.canGoBack) {
                        TransparentIconActionButton(
                            icon = MyIcons.back,
                            contentDescription = Res.string.back.asStringSource(),
                        ) {
                            menuStack.pop()
                        }
                    }
                    TransparentIconActionButton(
                        MyIcons.close,
                        Res.string.close.asStringSource()
                    ) {
                        onDismissRequest()
                    }
                }
            )
        }
    ) {
        BaseStackedMenu(
            menuStack = menuStack,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun RenderMenuInSheet(
    menu: MenuItem.SubMenu?,
    onDismissRequest: () -> Unit,
) {
    val responsiveDialogState = rememberResponsiveDialogState(false)
    LaunchedEffect(menu) {
        if (menu != null) {
            responsiveDialogState.show()
        } else {
            responsiveDialogState.hide()
        }
    }
    responsiveDialogState.OnFullyDismissed {
        onDismissRequest()
    }
    val hideDialog = responsiveDialogState::hide
    menu?.let {
        ResponsiveDialog(
            responsiveDialogState,
            hideDialog,
        ) {
            val menuStackState = rememberMenuStack(it)
            RenderMenuInSheetUi(menuStackState, hideDialog)
        }
    }
}
