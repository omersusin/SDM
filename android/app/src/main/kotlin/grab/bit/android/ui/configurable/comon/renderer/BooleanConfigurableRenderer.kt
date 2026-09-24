package grab.bit.android.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.configurable.item.BooleanConfigurable
import grab.bit.shared.ui.widget.CheckBox
import grab.bit.shared.ui.widget.Switch

object BooleanConfigurableRenderer : ConfigurableRenderer<BooleanConfigurable> {
    @Composable
    override fun RenderConfigurable(
        configurable: BooleanConfigurable,
        configurableUiProps: ConfigurableUiProps
    ) {
        RenderBooleanConfig(configurable, configurableUiProps)
    }

    @Composable
    private fun RenderBooleanConfig(
        cfg: BooleanConfigurable,
        configurableUiProps: ConfigurableUiProps,
    ) {
        val checked = cfg.stateFlow.collectAsState().value
        val setValue = cfg::set
        val enabled = isConfigEnabled()
        ConfigTemplate(
            modifier = configurableUiProps.modifier
                .clickable {
                    setValue(!checked)
                }
                .padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                when (cfg.renderMode) {
                    BooleanConfigurable.RenderMode.Checkbox -> {
                        CheckBox(
                            value = checked,
                            enabled = enabled,
                            onValueChange = {
                                setValue(it)
                            }
                        )
                    }

                    BooleanConfigurable.RenderMode.Switch -> {
                        Switch(
                            checked = checked,
                            enabled = enabled,
                            onCheckedChange = {
                                setValue(it)
                            }
                        )
                    }
                }
            })
    }
}
