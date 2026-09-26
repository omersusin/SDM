package grab.bit.android.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState

@Composable
fun rememberIsUiVisible(): Boolean {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentState by lifecycleOwner.lifecycle.currentStateAsState()
    return currentState.isAtLeast(Lifecycle.State.RESUMED)
}
