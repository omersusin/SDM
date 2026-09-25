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
import grab.bit.shared.ui.configurable.item.IntConfigurable
import grab.bit.shared.ui.widget.IntTextField

object IntConfigurableRenderer : ConfigurableRenderer<IntConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: IntConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderIntegerConfig(configurable, configurableUiProps)
    }


    private operator fun IntRange.get(index: Int): Int {
        return (start + index).also {
            if (it > last) {
                throw IndexOutOfBoundsException("$it bigger that $last")
            }
        }

    }

    @Composable
    private fun RenderIntegerConfig(cfg: IntConfigurable, configurableUiProps: ConfigurableUiProps) {
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
                    IntConfigurable.RenderMode.TextField -> {
                        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
                        IntTextField(
                            value = value,
                            onValueChange = { v ->
                                setValue(v)
                            },
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        backgroundColor = Color.Transparent
//                    ),
                            interactionSource = interactionSource,
                            range = cfg.range,
                            modifier = Modifier.width(100.dp),
                            enabled = enabled,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = if (cfg.range.last <= 100_000) "${cfg.range.first}–${cfg.range.last}" else "",
                        )
                    }
                }
            })
    }
}
