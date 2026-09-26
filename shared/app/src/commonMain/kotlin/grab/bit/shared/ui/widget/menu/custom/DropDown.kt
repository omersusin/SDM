package grab.bit.shared.ui.widget.menu.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import grab.bit.shared.ui.widget.rememberMyComponentRectPositionProvider

@Composable
fun MyDropDown(
    onDismissRequest: () -> Unit,
    offset: DpOffset = DpOffset.Zero,
    anchor: Alignment = Alignment.BottomStart,
    alignment: Alignment = Alignment.BottomEnd,
    focusable: Boolean = true,
    content: @Composable () -> Unit,
) {
    val positionProvider = rememberMyComponentRectPositionProvider(
        offset = offset,
        anchor = anchor,
        alignment = alignment,
    )
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = focusable),
        content = {
            AnimatedVisibility(
                true,
                enter = fadeIn() + scaleIn(.95f),
                exit = fadeOut() + scaleOut(),
                label = "drop",
            ) {
                content()
            }
        })
}

@Composable
fun SiblingDropDown(
    onDismissRequest: () -> Unit,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable () -> Unit,
) {
    val positionProvider = rememberMyComponentRectPositionProvider(
        anchor = Alignment.TopEnd,
        alignment = Alignment.BottomEnd,
        offset = offset,
    )
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
    ) {
        content()
    }
}
