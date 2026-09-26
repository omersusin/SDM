package grab.bit.shared.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExpandableItem(
    isExpanded:Boolean,
    header:@Composable ()->Unit,
    body: @Composable () -> Unit,
    modifier: Modifier = Modifier,
){
    Column(modifier) {
        header()
        AnimatedVisibility(
            isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
            label = "expand",
        ) {
            body()
        }
    }
}