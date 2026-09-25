package grab.bit.shared.ui.configurable.item

import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IntConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<Int>,
    describe: ((Int) -> StringSource),
    val range: IntRange,
    val renderMode: RenderMode = RenderMode.TextField,
    val step: Int = 1,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<Int>(
    title = title,
    description = description,
    backedBy = backedBy,
    validate = {
        it in range
    },
    describe = describe,
) {
    object Key : Configurable.Key

    override fun getKey() = Key

    enum class RenderMode {
        TextField,
        Stepper,
    }
}

