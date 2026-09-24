package grab.bit.android.pages.queue

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.SheetHeader
import grab.bit.android.ui.SheetTitle
import grab.bit.android.ui.SheetUI
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableGroup
import grab.bit.shared.ui.configurable.RenderConfigurableGroup
import grab.bit.shared.util.OnFullyDismissed
import grab.bit.shared.util.ResponsiveDialog
import grab.bit.shared.util.ResponsiveDialogScope
import grab.bit.shared.util.rememberResponsiveDialogState
import grab.bit.util.compose.resources.myStringResource

@Composable
fun QueueConfigSheet(
    queuesConfigurationComponent: QueueConfigurationComponent?,
    onDismiss: () -> Unit,
) {
    val state = rememberResponsiveDialogState(false)
    LaunchedEffect(
        queuesConfigurationComponent
    ) {
        if (queuesConfigurationComponent != null) {
            state.show()
        } else {
            state.hide()
        }
    }
    state.OnFullyDismissed(onDismiss)
    ResponsiveDialog(state, onDismiss = state::hide) {
        queuesConfigurationComponent?.let {
            QueueConfig(
                name = it.downloadQueue.queueModel.collectAsState().value.name,
                groups = it.configurations,
                onDismissRequest = state::hide,
            )
        }
    }
}

@Composable
private fun ResponsiveDialogScope.QueueConfig(
    name: String,
    groups: List<ConfigurableGroup>,
    onDismissRequest: () -> Unit,
) {
    SheetUI(
        header = {
            SheetHeader(
                headerTitle = {
                    val queues = myStringResource(Res.string.queues)
                    SheetTitle("${queues}: $name")
                }
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            for (group in groups) {
                RenderConfigurableGroup(
                    modifier = Modifier,
                    group = group,
                    itemPadding = PaddingValues(8.dp)
                )
            }
        }
    }
}
