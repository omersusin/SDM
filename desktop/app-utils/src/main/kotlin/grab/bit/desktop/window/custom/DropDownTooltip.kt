package grab.bit.desktop.window.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.Tooltip
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource

@Composable
private fun SystemButtonTooltip(
    stringSource: StringSource,
    content: @Composable () -> Unit,
) {
    Tooltip(
        tooltip = stringSource,
        anchor = Alignment.BottomCenter,
        alignment = Alignment.BottomCenter,
        content = content,
    )
}

@Composable
internal fun WindowCloseButtonTooltip(
    content: @Composable () -> Unit
) {
    SystemButtonTooltip(
        stringSource = Res.string.window_close.asStringSource(),
    ) {
        content()
    }
}

@Composable
internal fun WindowToggleMaximizeTooltip(
    content: @Composable () -> Unit
) {
    SystemButtonTooltip(
        stringSource = if (isWindowMaximized()) {
            Res.string.window_restore
        } else {
            Res.string.window_maximize
        }.asStringSource(),
    ) {
        content()
    }
}

@Composable
internal fun WindowMinimizeTooltip(
    content: @Composable () -> Unit
) {
    SystemButtonTooltip(
        stringSource = Res.string.window_minimize.asStringSource(),
    ) {
        content()
    }
}
