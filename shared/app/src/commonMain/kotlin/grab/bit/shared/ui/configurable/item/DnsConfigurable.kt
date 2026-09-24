package grab.bit.shared.ui.configurable.item

import grab.bit.shared.storage.DnsSettings
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DnsConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<DnsSettings>,
    describe: (DnsSettings) -> StringSource,
    validate: (DnsSettings) -> Boolean,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<DnsSettings>(
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
