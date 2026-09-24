package grab.bit.shared.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import grab.bit.shared.util.div
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.util.compose.IconSource
import grab.bit.util.compose.StringSource


@Composable
fun PrimaryMainIconButton(
    icon: IconSource,
    contentDescription: StringSource,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val backgroundColor = Brush.horizontalGradient(
        myColors.primaryGradientColors.map {
            it / 30
        }
    )
    val borderColor = Brush.horizontalGradient(
        myColors.primaryGradientColors
    )
    val disabledBorderColor = Brush.horizontalGradient(
        myColors.primaryGradientColors.map {
            it / 50
        }
    )
    IconActionButtonWithBrush(
        icon = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        backgroundColor = backgroundColor,
        disabledBackgroundColor = backgroundColor,
        borderColor = borderColor,
        disabledBorderColor = disabledBorderColor,
    )
}
