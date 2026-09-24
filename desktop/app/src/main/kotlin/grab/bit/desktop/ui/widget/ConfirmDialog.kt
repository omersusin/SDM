package grab.bit.desktop.ui.widget

import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.desktop.window.custom.WindowTitle
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myTextSizes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.shared.ui.widget.ActionContainer
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.resources.Res
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.desktop.screen.applyUiScale

@Suppress("unused")
sealed class ConfirmDialogType {
    data object Success : ConfirmDialogType()
    data object Info : ConfirmDialogType()
    data object Error : ConfirmDialogType()
    data object Warning : ConfirmDialogType()
}

@Composable
fun ConfirmDialog(
    title: StringSource,
    message: StringSource,
    type: ConfirmDialogType,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val uiScale = LocalUiScale.current
    val h = 180.applyUiScale(uiScale)
    val w = 400.applyUiScale(uiScale)
    val state = rememberWindowState(
        initialBoundsProvider = WindowBoundsProvider(
            sizeProvider = WindowSizeProvider.Fixed(
                size = DpSize(w.dp, h.dp),
            ),
            positionProvider = WindowPositionProvider.CenteredOnScreen
        )


    )
    CustomWindow(
        state,
        onRequestMinimize = null,
        onRequestToggleMaximize = null,
        onCloseRequest = onCancel,
        alwaysOnTop = true,
        minSize = DpSize(w.dp, h.dp)
    ) {
        val typeName = type.toString()
        WindowTitle(typeName)
        Column {
            Row(
                Modifier
                    .weight(1f)
                    .padding(8.dp),
            ) {
                val color = when (type) {
                    ConfirmDialogType.Error -> myColors.info
                    ConfirmDialogType.Info -> myColors.warning
                    ConfirmDialogType.Success -> myColors.success
                    ConfirmDialogType.Warning -> myColors.warning
                }
                MyIcon(
                    icon = MyIcons.info,
                    tint = color,
                    modifier = Modifier
                        .padding(16.dp)
                        .requiredSize(36.dp),
                    contentDescription = null,
                )
                Column {
                    Text(
                        title.rememberString(),
                        fontSize = myTextSizes.xl,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        message.rememberString(),
                        fontSize = myTextSizes.base,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            ActionContainer(
                Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    val confirmFocusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) {
                        confirmFocusRequester.requestFocus()
                    }
                    ActionButton(
                        myStringResource(Res.string.ok),
                        onClick = onConfirm,
                        modifier = Modifier.focusRequester(confirmFocusRequester)
                    )
                    Spacer(Modifier.width(8.dp))
                    ActionButton(
                        myStringResource(Res.string.cancel),
                        onClick = onCancel
                    )
                }
            }
        }

    }
}
