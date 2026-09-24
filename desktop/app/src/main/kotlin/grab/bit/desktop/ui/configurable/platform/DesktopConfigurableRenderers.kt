package grab.bit.desktop.ui.configurable.platform

import grab.bit.desktop.ui.configurable.platform.item.FontConfigurable
import grab.bit.shared.ui.configurable.item.ProxyConfigurable
import grab.bit.desktop.ui.configurable.platform.renderer.FontConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.ProxyConfigurableRenderer
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ContainsConfigurableRenderers

data class DesktopConfigurableRenderers(
    val fontConfigurableRenderer: ConfigurableRenderer<FontConfigurable>,
) : ContainsConfigurableRenderers {
    override fun getAllRenderers(): Map<Configurable.Key, ConfigurableRenderer<*>> {
        return mapOf(
            FontConfigurable.Key to fontConfigurableRenderer,
        )
    }
}

val PlatformConfigurableRenderersForDesktop = DesktopConfigurableRenderers(
    fontConfigurableRenderer = FontConfigurableRenderer,
)
