package grab.bit.shared.util.ui.widget

import androidx.compose.runtime.Composable

@Composable
expect fun MPBackHandler(
    isEnabled: Boolean = true,
    onBack: () -> Unit,
)
