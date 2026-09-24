package grab.bit.shared.ui.configurable.item

import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.util.proxy.ProxyData
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProxyConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<ProxyData>,
    describe: (ProxyData) -> StringSource,
    validate: (ProxyData) -> Boolean,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<ProxyData>(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = describe,
    validate = validate,
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key
}
