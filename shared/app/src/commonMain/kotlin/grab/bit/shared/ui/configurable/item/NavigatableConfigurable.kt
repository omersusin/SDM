package grab.bit.shared.ui.configurable.item

import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigatableConfigurable(
    title: StringSource,
    description: StringSource,
    val onRequestNavigate: () -> Unit,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<Unit>(
    title = title,
    description = description,
    backedBy = MutableStateFlow(Unit),
    describe = { "".asStringSource() },
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key
}
