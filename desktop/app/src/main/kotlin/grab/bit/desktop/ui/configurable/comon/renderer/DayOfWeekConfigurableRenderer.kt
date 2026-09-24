package grab.bit.desktop.ui.configurable.comon.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import grab.bit.resources.Res
import grab.bit.desktop.ui.configurable.ConfigTemplate
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.desktop.ui.configurable.TitleAndDescription
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.isConfigEnabled
import grab.bit.shared.ui.configurable.item.DayOfWeekConfigurable
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.div
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.util.compose.asStringSource
import grab.bit.util.ifThen
import kotlinx.datetime.DayOfWeek

object DayOfWeekConfigurableRenderer : ConfigurableRenderer<DayOfWeekConfigurable> {
    @Composable
    override fun RenderConfigurable(configurable: DayOfWeekConfigurable, configurableUiProps: ConfigurableUiProps) {
        RenderDayOfWeekConfigurable(configurable, configurableUiProps)
    }

    @Composable
    private fun RenderDayOfWeekConfigurable(cfg: DayOfWeekConfigurable, configurableUiProps: ConfigurableUiProps) {
        val value by cfg.stateFlow.collectAsState()
        val setValue = cfg::set
        val allDays = DayOfWeek.entries.toSet()
        val enabled = isConfigEnabled()
        fun isSelected(dayOfWeek: DayOfWeek): Boolean {
            return dayOfWeek in value
        }

        fun selectDay(dayOfWeek: DayOfWeek, select: Boolean) {
            if (!enabled) return
            if (select) {
                setValue(
                    value.plus(dayOfWeek).sorted().toSet()
                )
            } else {
                setValue(
                    value.minus(dayOfWeek).sorted().toSet()
                )
            }
        }
        ConfigTemplate(
            modifier = configurableUiProps.modifier.padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(cfg, true)
            },
            value = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier.ifThen(!enabled) {
                            alpha(0.5f)
                        }
                    ) {
                        FlowRow(Modifier.fillMaxWidth()) {
                            allDays.forEach { dayOfWeek ->
                                RenderDayOfWeek(
                                    modifier = Modifier,
                                    enabled = enabled,
                                    dayOfWeek = dayOfWeek,
                                    selected = isSelected(dayOfWeek),
                                    onSelect = { s, isSelected ->
                                        selectDay(dayOfWeek, isSelected)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun RenderDayOfWeek(
        modifier: Modifier,
        dayOfWeek: DayOfWeek,
        selected: Boolean,
        onSelect: (DayOfWeek, Boolean) -> Unit,
        enabled: Boolean = true,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(2.dp)
                .clip(CircleShape)
                .ifThen(selected) {
                    background(myColors.onBackground / 10)
                }
                .clickable(enabled = enabled) {
                    onSelect(dayOfWeek, !selected)
                }
                .padding(vertical = 4.dp)
                .padding(horizontal = 8.dp)

        ) {
            MyIcon(
                MyIcons.check,
                null,
                Modifier.size(10.dp)
                    .alpha(if (selected) 1f else 0f),
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = dayOfWeek.asStringSource().rememberString(),
                modifier = Modifier.alpha(
                    if (selected) 1f
                    else 0.5f
                ),
                softWrap = false,
                fontSize = myTextSizes.base,
            )
        }
    }

    private fun DayOfWeek.asStringSource() = when (this) {
        DayOfWeek.MONDAY -> Res.string.monday
        DayOfWeek.TUESDAY -> Res.string.tuesday
        DayOfWeek.WEDNESDAY -> Res.string.wednesday
        DayOfWeek.THURSDAY -> Res.string.thursday
        DayOfWeek.FRIDAY -> Res.string.friday
        DayOfWeek.SATURDAY -> Res.string.saturday
        DayOfWeek.SUNDAY -> Res.string.sunday
    }.asStringSource()
}
