package grab.bit.android.pages.newqueue

import androidx.compose.runtime.Composable
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource

@Composable
fun NewQueueSheet(
    onQueueCreate: (String) -> Unit,
    isOpened: Boolean,
    onCloseRequest: () -> Unit,
) {
    SheetInput(
        title = Res.string.add_new_queue.asStringSource(),
        validate = { it.isNotEmpty() },
        isOpened = isOpened,
        initialValue = { "" },
        onDismiss = onCloseRequest,
        onConfirm = onQueueCreate,
        inputContent = {
            MyTextField(
                modifier = it.modifier,
                text = it.editingValue,
                onTextChange = it.setEditingValue,
                placeholder = myStringResource(Res.string.queue_name),
            )
        },
    )
}
