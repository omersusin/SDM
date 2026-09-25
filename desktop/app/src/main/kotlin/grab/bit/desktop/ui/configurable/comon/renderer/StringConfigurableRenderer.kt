package grab.bit.desktop.ui.configurable.comon.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.PresetChipRow
import grab.bit.shared.ui.configurable.item.StringConfigurable
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource

object StringConfigurableRenderer : ConfigurableRenderer<StringConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: StringConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderStringConfig(configurable, configurableUiProps)
    }

    @Composable
    fun RenderStringConfig(cfg: StringConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set
        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                Column {
                    MyTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = value,
                        onTextChange = {
                            setValue(it)
                        },
                        shape = myShapes.defaultRounded,
                        textPadding = PaddingValues(4.dp),
                        placeholder = cfg.placeholder?.rememberString().orEmpty(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (cfg.secret) KeyboardType.Password else KeyboardType.Text,
                        ),
                        visualTransformation = if (cfg.secret) PasswordVisualTransformation() else VisualTransformation.None,
                    )
                    if (cfg.presets.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        PresetChipRow(
                            presets = cfg.presets,
                            selected = value,
                            onPick = { setValue(it) },
                            labels = cfg.presetLabels,
                        )
                    }
                    if (cfg.defaultValue != null && value != cfg.defaultValue) {
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Spacer(Modifier.weight(1f))
                            ActionButton(
                                text = myStringResource(Res.string.settings_reset_to_default),
                                onClick = { setValue(cfg.defaultValue) },
                            )
                        }
                    }
                }
            }
        )
    }
}
