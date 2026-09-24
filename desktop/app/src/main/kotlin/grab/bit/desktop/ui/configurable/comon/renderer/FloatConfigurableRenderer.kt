package grab.bit.desktop.ui.configurable.comon.renderer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.configurable.item.FloatConfigurable
import grab.bit.shared.ui.widget.FloatTextField

object FloatConfigurableRenderer : ConfigurableRenderer<FloatConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: FloatConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderFloatConfig(configurable, configurableUiProps)
    }

    @Composable
    private fun RenderFloatConfig(cfg: FloatConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set
        val enabled = isConfigEnabled()
        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                when (cfg.renderMode) {
                    FloatConfigurable.RenderMode.TextField -> {
                        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

                        val modifier = Modifier.Companion.width(100.dp)
                        FloatTextField(
                            value = value,
                            onValueChange = { v ->
                                setValue(v)
                            },
                            interactionSource = interactionSource,
                            range = cfg.range,
                            modifier = modifier,
                            enabled = enabled,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Decimal),
                            placeholder = "",
                        )
                    }
                }
            }
        )
    }

}
