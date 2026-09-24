package grab.bit.android.ui.configurable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitle
import grab.bit.android.ui.SheetUI
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.util.compose.StringSource

@Composable
fun ConfigurableSheet(
    title: StringSource,
    isOpened: Boolean,
    onDismiss: () -> Unit,
    headerActions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    val dialogState = rememberResponsiveDialogState(isOpened)
    LaunchedEffect(isOpened) {
        when (isOpened) {
            true -> dialogState.show()
            false -> dialogState.hide()
        }
    }
    dialogState.OnFullyDismissed {
        onDismiss()
    }
    ResponsiveDialog(
        state = dialogState,
        onDismiss = dialogState::hide,
    ) {
        SheetUI(
            header = {
                SheetHeader(
                    headerTitle = {
                        SheetTitle(
                            title.rememberString()
                        )
                    },
                    headerActions = headerActions,
                )
            }
        ) {
            content()
        }
    }
}
