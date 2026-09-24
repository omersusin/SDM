package grab.bit.android.pages.add.shared

import grab.bit.shared.ui.configurable.RenderConfigurable
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.div
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitle
import grab.bit.android.ui.SheetUI
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.shared.util.ui.MultiplatformVerticalScrollbar
import grab.bit.shared.util.ui.theme.LocalUiScale
import grab.bit.shared.util.ui.theme.myShapes
import io.github.oikvpqya.compose.fastscroller.rememberScrollbarAdapter

@Composable
fun ExtraConfig(
    isOpened: Boolean,
    onDismiss: () -> Unit,
    configurables: List<Configurable<*>>,
) {
    val dialogState = rememberResponsiveDialogState(false)
    LaunchedEffect(isOpened) {
        if (isOpened) {
            dialogState.show()
        } else {
            dialogState.hide()
        }
    }
    dialogState.OnFullyDismissed {
        onDismiss()
    }
    ResponsiveDialog(
        state = dialogState,
        onDismiss = {
            dialogState.hide()
        }
    ) {
        SheetUI(
            header = {
                SheetHeader(
                    headerTitle = {
                        SheetTitle("Extra Config")
                    },
                    headerActions = {}
                )
            }
        ) {
            Box {
                val scrollState = rememberScrollState()
                Column(
                    Modifier.verticalScroll(scrollState)
                ) {
                    for ((index, cfg) in configurables.withIndex()) {
                        RenderConfigurable(
                            cfg,
                            ConfigurableUiProps(
                                itemPaddingValues = PaddingValues(vertical = 8.dp, horizontal = 32.dp)
                            )
                        )
                        if (index != configurables.lastIndex) {
                            Divider()
                        }
                    }
                }
                MultiplatformVerticalScrollbar(
                    rememberScrollbarAdapter(scrollState),
                    Modifier
                        .matchParentSize()
                        .wrapContentWidth()
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun Divider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(myColors.onBackground / 10),
    )
}
