package grab.bit.android.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.NextIcon
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.PresetChipRow
import grab.bit.shared.ui.configurable.item.StringConfigurable
import grab.bit.shared.ui.widget.ActionButton
import grab.bit.shared.ui.widget.MyTextField
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.shared.util.ui.widget.MyIcon

object StringConfigurableRenderer : ConfigurableRenderer<StringConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: StringConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderStringConfig(configurable, configurableUiProps)
    }

    @Composable
    fun RenderStringConfig(cfg: StringConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set
        var isOpened by remember { mutableStateOf(false) }
        val onDismiss = {
            isOpened = false
        }
        ConfigTemplate(
            modifier = configurableUiProps.modifier
                .clickable { isOpened = true }
                .padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                NextIcon()
            }
        )
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
        SheetInput(
            configurable = cfg,
            isOpened = isOpened,
            onDismiss = onDismiss,
            inputContent = { params ->
                Column {
                    MyTextField(
                        modifier = params.modifier.fillMaxWidth(),
                        text = params.editingValue,
                        onTextChange = {
                            params.setEditingValue(it)
                        },
                        shape = myShapes.defaultRounded,
                        textPadding = PaddingValues(8.dp),
                        placeholder = "",
                        interactionSource = interactionSource,
                        keyboardActions = params.keyboardActions,
                    )
                    if (cfg.presets.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        PresetChipRow(
                            presets = cfg.presets,
                            selected = params.editingValue,
                            onPick = { params.setEditingValue(it) },
                            labels = cfg.presetLabels,
                        )
                    }
                    val resetToDefault = cfg.defaultValue
                    if (resetToDefault != null && params.editingValue != resetToDefault) {
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Spacer(Modifier.weight(1f))
                            ActionButton(
                                text = myStringResource(Res.string.settings_reset_to_default),
                                onClick = {
                                    params.setEditingValue(resetToDefault)
                                },
                            )
                        }
                    }
                }
            },
            onConfirm = {
                cfg.set(it)
                onDismiss()
            },
        )

    }
}
