package grab.bit.android.pages.browser.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import grab.bit.android.storage.BrowserBookmark
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource

@Immutable
data class EditBookmarkState(
    val initialValue: BrowserBookmark,
    val editMode: Boolean = false,
)

@Composable
fun EditBookmarkSheet(
    state: EditBookmarkState,
    onSave: (BrowserBookmark) -> Unit,
    onCancel: () -> Unit,
) {
    val editMode = state.editMode
    val initialValue = state.initialValue
    val sheetTitle = if (editMode) Res.string.browser_edit_bookmark else Res.string.browser_add_bookmark
    SheetInput(
        title = sheetTitle.asStringSource(),
        validate = {
            it.title.isNotEmpty() && it.url.isNotEmpty()
        },
        isOpened = true,
        initialValue = { initialValue },
        onDismiss = onCancel,
        onConfirm = onSave,
        inputContent = { inputParams ->
            var title by remember(state) {
                mutableStateOf(state.initialValue.title)
            }
            var url by remember(state) {
                mutableStateOf(state.initialValue.url)
            }
            LaunchedEffect(url, title) {
                inputParams.setEditingValue(
                    BrowserBookmark(
                        url = url, title = title,
                    )
                )
            }
            Column(
                modifier = inputParams.modifier,
            ) {
                val (urlFR, titleFR) = remember { FocusRequester.createRefs() }
                LaunchedEffect(Unit) {
                    when {
                        url.isBlank() -> {
                            urlFR.requestFocus()
                        }

                        title.isBlank() -> {
                            titleFR.requestFocus()
                        }
                    }
                }
                val textFieldModifier = Modifier
                MyTextField(
                    text = url,
                    onTextChange = {
                        url = it
                    },
                    modifier = textFieldModifier
                        .focusRequester(urlFR),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions.Default,
                    placeholder = "URL",
                )
                Spacer(modifier = Modifier.height(mySpacings.mediumSpace))
                MyTextField(
                    text = title,
                    onTextChange = {
                        title = it
                    },
                    modifier = textFieldModifier
                        .focusRequester(titleFR),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = inputParams.keyboardActions,
                    placeholder = myStringResource(Res.string.name),
                )
            }
        },
    )
}
