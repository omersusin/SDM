package grab.bit.desktop.ui.configurable.comon.renderer

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import grab.bit.resources.Res
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.item.NavigatableConfigurable
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.util.compose.resources.myStringResource

object PerHostSettingsConfigurableRenderer : ConfigurableRenderer<NavigatableConfigurable> {
    @Composable
    override fun RenderConfigurable(
        configurable: NavigatableConfigurable,
        configurableUiProps: ConfigurableUiProps
    ) {
        RenderPerHostSettingsConfigurable(
            cfg = configurable,
            configurableUiProps = configurableUiProps,
            onRequestOpenConfigWindow = configurable.onRequestNavigate,
        )
    }

    @Composable
    private fun RenderPerHostSettingsConfigurable(
        cfg: NavigatableConfigurable,
        configurableUiProps: ConfigurableUiProps,
        onRequestOpenConfigWindow: () -> Unit
    ) {
//    val value by cfg.stateFlow.collectAsState()
//    val setValue = cfg::set
//    val enabled = isConfigEnabled()

        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                ActionButton(
                    myStringResource(Res.string.change),
                    onClick = onRequestOpenConfigWindow,
                )
            },
        )
    }

}
