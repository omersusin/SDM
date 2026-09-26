package grab.bit.android.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.NextIcon
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.configurable.item.IntConfigurable
import grab.bit.shared.ui.configurable.item.LongConfigurable
import grab.bit.shared.ui.widget.IntTextField
import grab.bit.shared.ui.widget.LongTextField
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.widget.MyIcon

object LongConfigurableRenderer : ConfigurableRenderer<LongConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: LongConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderLongConfig(configurable, configurableUiProps)
    }


    @Composable
    private fun RenderLongConfig(cfg: LongConfigurable, configurableUiProps: ConfigurableUiProps) {

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
                when (cfg.renderMode) {
                    LongConfigurable.RenderMode.TextField -> {
                        NextIcon()

                        RenderTextFieldIntInput(
                            cfg = cfg, isOpened = isOpened, onDismiss = onDismiss
                        )
                    }
                }
            })
    }

    @Composable
    fun RenderTextFieldIntInput(
        cfg: LongConfigurable,
        isOpened: Boolean,
        onDismiss: () -> Unit,
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
        SheetInput(
            configurable = cfg,
            isOpened = isOpened,
            onDismiss = onDismiss,
            inputContent = { params ->
                LongTextField(
                    value = params.editingValue,
                    onValueChange = { v ->
                        params.setEditingValue(v)
                    },
                    interactionSource = interactionSource,
                    range = cfg.range,
                    modifier = params.modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = params.keyboardActions,
                    textPadding = PaddingValues(8.dp),
                    placeholder = if (cfg.range.last <= 100_000L) "${cfg.range.first}–${cfg.range.last}" else "",
                )
            },
            onConfirm = {
                cfg.set(it)
                onDismiss()
            },
        )
    }

}
