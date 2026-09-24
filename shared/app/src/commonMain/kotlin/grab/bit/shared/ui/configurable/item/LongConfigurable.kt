package grab.bit.shared.ui.configurable.item

import grab.bit.shared.ui.configurable.BaseLongConfigurable
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LongConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<Long>,
    describe: ((Long) -> StringSource),
    range: LongRange,
    val renderMode: RenderMode = RenderMode.TextField,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : BaseLongConfigurable(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = describe,
    range = range,
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key

    enum class RenderMode {
        TextField,
    }
}
