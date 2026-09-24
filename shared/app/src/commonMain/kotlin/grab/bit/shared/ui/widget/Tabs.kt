package grab.bit.shared.ui.widget

import grab.bit.util.compose.IconSource
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.shared.util.ui.WithContentAlpha
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.util.compose.StringSource
import grab.bit.util.ifThen


@Composable
fun MyTabRow(content: @Composable RowScope.() -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ) {
        content()
    }
}

@Composable
fun MyTab(
    selected: Boolean,
    onClick: () -> Unit,
    icon: IconSource,
    title: StringSource,
    selectionBackground: Color = myColors.surface,
) {
    WithContentAlpha(
        if (selected) 1f else 0.75f
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .ifThen(selected) {
                    background(selectionBackground)
                }
                .clickable { onClick() }
                .heightIn(mySpacings.thumbSize)
                .padding(horizontal = 12.dp)
                .padding(vertical = 6.dp)

        ) {
            MyIcon(icon, null, Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                title.rememberString(),
                maxLines = 1,
                fontSize = myTextSizes.base,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        }
    }
}
