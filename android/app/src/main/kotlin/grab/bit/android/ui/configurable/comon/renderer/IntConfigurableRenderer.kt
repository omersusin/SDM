package grab.bit.android.ui.configurable.comon.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.NextIcon
import grab.bit.android.ui.configurable.SheetInput
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.configurable.item.FloatConfigurable
import grab.bit.shared.ui.configurable.item.IntConfigurable
import grab.bit.shared.ui.widget.FloatTextField
import grab.bit.shared.ui.widget.IconActionButton
import grab.bit.shared.ui.widget.IntTextField
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.ifThen

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
//        val value by cfg.stateFlow.collectAsState()
//        val setValue = cfg::set
//        val enabled = isConfigEnabled()

        var isOpened by remember { mutableStateOf(false) }
        val onDismiss = {
            isOpened = false
        }

        ConfigTemplate(
            modifier = configurableUiProps.modifier
                .ifThen(cfg.renderMode == IntConfigurable.RenderMode.TextField) {
                    clickable { isOpened = true }
                }
                .padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                when (cfg.renderMode) {
                    IntConfigurable.RenderMode.TextField -> {
                        NextIcon()
                        RenderTextFieldIntInput(cfg = cfg, isOpened = isOpened, onDismiss = onDismiss)
                    }

                    IntConfigurable.RenderMode.Stepper -> {
                        RenderStepperIntInput(cfg = cfg)
                    }
                }
            })
    }

    @Composable
    fun RenderStepperIntInput(cfg: IntConfigurable) {
        val value by cfg.stateFlow.collectAsState()
        val enabled = isConfigEnabled()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconActionButton(
                MyIcons.minus,
                Res.string.settings_stepper_decrease.asStringSource(),
                enabled = enabled && value > cfg.range.first,
                onClick = { cfg.set(value - cfg.step) },
            )
            Text(
                "$value",
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.Center,
            )
            IconActionButton(
                MyIcons.add,
                Res.string.settings_stepper_increase.asStringSource(),
                enabled = enabled && value < cfg.range.last,
                onClick = { cfg.set(value + cfg.step) },
            )
        }
    }

    @Composable
    fun RenderTextFieldIntInput(
        cfg: IntConfigurable,
        isOpened: Boolean,
        onDismiss: () -> Unit,
    ) {
        val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

        SheetInput(
            configurable = cfg,
            isOpened = isOpened,
            onDismiss = onDismiss,
            inputContent = { params ->
                IntTextField(
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
                    placeholder = if (cfg.range.last <= 100_000) "${cfg.range.first}–${cfg.range.last}" else "",
                )
            },
            onConfirm = {
                cfg.set(it)
                onDismiss()
            },
        )

    }
}
