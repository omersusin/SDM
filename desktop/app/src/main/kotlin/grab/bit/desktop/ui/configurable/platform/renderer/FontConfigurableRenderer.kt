package grab.bit.desktop.ui.configurable.platform.renderer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import grab.bit.desktop.ui.configurable.platform.item.FontConfigurable
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.RenderSpinner
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.util.ifThen

object FontConfigurableRenderer : ConfigurableRenderer<FontConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: FontConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderFontConfig(configurable, configurableUiProps)
    }

    @Composable
    private fun RenderFontConfig(cfg: FontConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set
        val enabled = isConfigEnabled()
        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                RenderSpinner(
                    possibleValues = cfg.possibleValues, value = value, onSelect = {
                        setValue(it)
                    },
                    valueToString = cfg.valueToString,
                    modifier = Modifier.widthIn(min = 160.dp),
                    enabled = enabled,
                    render = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.ifThen(!enabled) {
                                alpha(0.5f)
                            }
                        ) {
                            Text(
                                cfg.describe(it).rememberString(),
                                fontFamily = it.fontFamily,
                                fontSize = myTextSizes.lg,
                            )
                        }
                    })
            }
        )
    }

}
