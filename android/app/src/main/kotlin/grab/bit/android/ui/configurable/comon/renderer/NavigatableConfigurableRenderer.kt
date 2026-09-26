package grab.bit.android.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.NextIcon
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.item.NavigatableConfigurable
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.widget.MyIcon

object NavigatableConfigurableRenderer : ConfigurableRenderer<NavigatableConfigurable> {
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

        ConfigTemplate(
            modifier = configurableUiProps.modifier
                .clickable {
                    onRequestOpenConfigWindow()
                }
                .padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                NextIcon()
            },
        )
    }

}
