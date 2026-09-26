package grab.bit.shared.ui.widget

import grab.bit.shared.util.ui.LocalContentColor
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.util.ifThen
import grab.bit.shared.util.div
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import grab.bit.resources.Res
import grab.bit.shared.util.ui.WithContentAlpha
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.util.compose.modifiers.silentClickable
import grab.bit.util.compose.resources.myStringResource

@Composable
fun CheckBox(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    uncheckedAlpha: Float = 0.25f,
    shape: Shape = RoundedCornerShape(25)
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier
            .ifThen(!enabled) {
                alpha(0.5f)
            }
            .size(size)
            .clip(shape)
            .triStateToggleable(
                state = ToggleableState(value),
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null,
                onClick = { onValueChange(!value) },
            )
    ) {
        val borderColor = if (isFocused) {
            myColors.focusedBorderColor
        } else {
            LocalContentColor.current / uncheckedAlpha
        }
        Spacer(
            Modifier.matchParentSize()
                .border(1.dp, borderColor, shape)
        )
        AnimatedContent(
            value,
            transitionSpec = {
                val tween = tween<Float>(220)
                fadeIn(tween) togetherWith fadeOut(tween)
            },
            label = "check",
        ) {
            val m = Modifier
                .fillMaxSize()
                .background(myColors.primaryGradient)
            if (it) {
                MyIcon(
                    MyIcons.check,
                    contentDescription = null,
                    modifier = m,
                    tint = myColors.onPrimaryGradient,
                )
            } else {
                Spacer(m)
            }
        }
    }
}

@Composable
fun LabeledCheckbox(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    description: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .silentClickable(
                enabled = enabled,
            ) {
                onValueChange(!value)
            }
            .heightIn(min = mySpacings.thumbSize),
    ) {
        CheckBox(
            size = mySpacings.iconSize,
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
        )
        Spacer(Modifier.width(mySpacings.mediumSpace))
        WithContentAlpha(
            if (enabled) 1f else 0.5f,
        ) {
            Text(description)
        }
    }
}
