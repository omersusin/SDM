package grab.bit.android.ui.configurable.android.item

import grab.bit.android.pages.onboarding.permissions.AppPermission
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermissionConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<AppPermission>,
    describe: () -> StringSource = { "".asStringSource() },
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<AppPermission>(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = {
        describe()
    },
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key
}
