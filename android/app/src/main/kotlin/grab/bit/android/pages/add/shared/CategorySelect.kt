package grab.bit.android.pages.add.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.*
import grab.bit.android.ui.configurable.RenderSpinnerInSheet
import grab.bit.resources.Res
import grab.bit.shared.ui.widget.IconActionButton
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.util.ifThen
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.div
import grab.bit.shared.util.category.Category
import grab.bit.shared.util.category.rememberIconPainter
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.shared.util.ui.widget.MyIcon
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource

@Composable
fun CategorySelect(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
) {
    var isSelectionOpen by remember {
        mutableStateOf(false)
    }
    val closeDialog = {
        isSelectionOpen = false
    }
    RenderSelectedCategory(
        modifier = modifier,
        item = selectedCategory,
        enabled = enabled,
        onClick = {
            isSelectionOpen = true
        },
        renderItem = {
            RenderCategory(
                category = it,
                modifier = Modifier,
            )
        }
    )
    selectedCategory?.let {
        RenderSpinnerInSheet(
            title = Res.string.categories.asStringSource(),
            isOpened = isSelectionOpen,
            onDismiss = closeDialog,
            possibleValues = categories,
            render = {
                RenderCategory(
                    category = it,
                    modifier = Modifier,
                )
            },
            value = selectedCategory,
            onSelect = {
                onCategorySelected(it)
            },
        )
    }
}

@Composable
private fun RenderCategory(
    modifier: Modifier,
    category: Category,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = category.rememberIconPainter()
        val iconModifier = Modifier.size(16.dp)
        if (icon != null) {
            MyIcon(
                icon,
                null,
                iconModifier,
            )
        } else {
            Spacer(iconModifier)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            category.name,
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryAddButton(
    modifier: Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconActionButton(
        modifier = modifier,
        icon = MyIcons.add,
        contentDescription = Res.string.add_category.asStringSource(),
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun <T> RenderSelectedCategory(
    item: T?,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    renderItem: @Composable (T) -> Unit,
) {
    val borderColor = myColors.onBackground / 0.1f
//    val background = myColors.surface / 50
    val shape = myShapes.defaultRounded
    Row(
        modifier
            .height(IntrinsicSize.Max)
            .heightIn(mySpacings.thumbSize)
            .clip(shape)
            .ifThen(!enabled) {
                alpha(0.5f)
            }
            .border(1.dp, borderColor, shape)

//            .background(background)
            .clickable(
                enabled = enabled
            ) { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contentModifier = Modifier
            .padding(vertical = 8.dp)
            .weight(1f)
        if (item != null) {
            Box(contentModifier) {
                renderItem(item)
            }
        } else {
            Text(
                myStringResource(Res.string.no_category_selected),
                contentModifier
            )
        }
        Spacer(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .width(1.dp)
                .background(borderColor)
        )
        MyIcon(
            MyIcons.down,
            null,
            Modifier
                .align(Alignment.CenterVertically)
                .size(16.dp),
        )
    }
}
