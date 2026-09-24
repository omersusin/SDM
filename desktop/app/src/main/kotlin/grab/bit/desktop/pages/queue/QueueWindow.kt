package grab.bit.desktop.pages.queue

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.v2.rememberWindowState
import grab.bit.desktop.AppComponent
import grab.bit.desktop.window.custom.CustomWindow
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.rememberChild

@Composable
fun QueuesWindow(appComponent: AppComponent) {
    appComponent.showQueuesSlot.rememberChild()?.let {
        QueuesWindow(it)
    }
}


@Composable
private fun QueuesWindow(queuesComponent: QueuesComponent) {
    val state = rememberWindowState()
    CustomWindow(
        state = state,
        onCloseRequest = queuesComponent.close
    ) {
        HandleEffects(queuesComponent) {
            if (it == QueuesComponentEffects.ToFront) {
                state.requestMinimized(false)
                window.toFront()
            }
        }
        QueuePage(queuesComponent)
    }
}
