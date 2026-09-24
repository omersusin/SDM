package grab.bit.shared.ui.configurable.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FloatConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<Float>,
    val range: ClosedFloatingPointRange<Float>,
    val steps: Int = 0,
    val renderMode: RenderMode = RenderMode.TextField,

    describe: ((Float) -> StringSource),
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<Float>(
    title = title,
    description = description,
    backedBy = backedBy,
    validate = { it in range },
    describe = describe,
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key

    enum class RenderMode {
        TextField,
    }
}

