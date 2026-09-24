package grab.bit.desktop.ui.widget

import grab.bit.desktop.AppComponent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import androidx.compose.ui.window.v2.WindowSizeProvider
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.MessageDialogType
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.desktop.screen.applyUiScale
import java.util.UUID

data class MessageDialogModel(
    val id: String = UUID.randomUUID().toString(),
    val title: StringSource,
    val description: StringSource,
    val type: MessageDialogType = MessageDialogType.Info,
)

@Composable
fun ShowMessageDialogs(
    appComponent: AppComponent,
) {
    val list by appComponent.dialogMessages.collectAsState()

    for (msg in list) {
        MessageDialog(
            msgContent = msg,
            onConfirm = {
                appComponent.onDismissDialogMessage(msg)
            }
        )
    }
}

@Composable
fun MessageDialog(
    msgContent: MessageDialogModel,
    onConfirm: () -> Unit,
) {
    val uiScale = LocalUiScale.current
    val h = 200.applyUiScale(uiScale)
    val w = 400.applyUiScale(uiScale)
    val state = rememberWindowState(
        initialBoundsProvider = WindowBoundsProvider(
            sizeProvider = WindowSizeProvider.Fixed(
                size = DpSize(w.dp, h.dp)
            ),
            positionProvider = WindowPositionProvider.CenteredOnScreen
        )
    )
    CustomWindow(
        state,
        onRequestMinimize = null,
        onRequestToggleMaximize = null,
        onCloseRequest = onConfirm,
        alwaysOnTop = true,
        minSize = DpSize(w.dp, h.dp)
    ) {
        val typeName = msgContent.type.toString()
        WindowTitle(typeName)
        Row(
            Modifier.padding(8.dp),
        ) {
            val color = when (msgContent.type) {
                MessageDialogType.Error -> myColors.info
                MessageDialogType.Info -> myColors.warning
                MessageDialogType.Success -> myColors.success
                MessageDialogType.Warning -> myColors.warning
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
                    msgContent.title.rememberString(),
                    fontSize = myTextSizes.xl,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    msgContent.description.rememberString(),
                    fontSize = myTextSizes.base,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    ActionButton(
                        myStringResource(Res.string.ok),
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}
