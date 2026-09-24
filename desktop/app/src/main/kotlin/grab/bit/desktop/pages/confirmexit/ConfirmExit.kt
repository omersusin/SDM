package grab.bit.desktop.pages.confirmexit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import grab.bit.desktop.AppComponent
import grab.bit.desktop.ui.widget.ConfirmDialog
import grab.bit.desktop.ui.widget.ConfirmDialogType
import grab.bit.resources.Res
import grab.bit.util.compose.asStringSource

@Composable
fun ConfirmExit(appComponent: AppComponent) {
    val showExitDialog by appComponent.showConfirmExitDialog.collectAsState()
    if (showExitDialog) {
        ConfirmDialog(
            Res.string.confirm_exit.asStringSource(),
            Res.string.confirm_exit_description.asStringSource(),
            onCancel = appComponent::closeConfirmExit,
            onConfirm = appComponent::exitAppAsync,
            type = ConfirmDialogType.Warning,
        )
    }
}