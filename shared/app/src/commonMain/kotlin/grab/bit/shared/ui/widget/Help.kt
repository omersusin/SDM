package grab.bit.shared.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.widget.MyIcon

@Composable
fun Help(
    content: String,
    modifier: Modifier = Modifier,
) {
    var showHelpContent by remember { mutableStateOf(false) }
    val onRequestCloseShowHelpContent = {
        showHelpContent = false
    }
    Column(modifier) {
        MyIcon(
            MyIcons.question,
            "Hint",
            Modifier
                .clip(CircleShape)
                .clickable {
                    showHelpContent = !showHelpContent
                }
                .border(
                    1.dp,
                    if (showHelpContent) myColors.primary
                    else Color.Transparent,
                    CircleShape
                )
                .background(myColors.surface)
                .padding(4.dp)
                .size(12.dp),
            tint = myColors.onSurface,
        )
        AnimatedVisibility(
            showHelpContent,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
            label = "help",
        ) {
            TooltipPopup(
                onRequestCloseShowHelpContent = onRequestCloseShowHelpContent,
                content = content,
            )
        }
    }
}
