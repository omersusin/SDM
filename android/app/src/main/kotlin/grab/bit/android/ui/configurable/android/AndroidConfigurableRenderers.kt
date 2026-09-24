package grab.bit.android.ui.configurable.android

import grab.bit.android.ui.configurable.android.item.PermissionConfigurable
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ContainsConfigurableRenderers

data class AndroidConfigurableRenderers(
    val permissionConfigurableRenderers: ConfigurableRenderer<PermissionConfigurable>,
) : ContainsConfigurableRenderers {
    override fun getAllRenderers(): Map<Configurable.Key, ConfigurableRenderer<*>> {
        return mapOf(
            PermissionConfigurable.Key to permissionConfigurableRenderers,
        )
    }
}
