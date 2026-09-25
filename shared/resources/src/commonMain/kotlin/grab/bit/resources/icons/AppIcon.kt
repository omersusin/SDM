package grab.bit.resources.icons

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ABDMIcons.AppIcon: ImageVector
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "AppIcon",
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 200f,
            viewportHeight = 200f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF0D3B2E)),
                pathFillType = PathFillType.NonZero
            ) {
                addRect(Rect(0f, 0f, 200f, 200f))
            }
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                // mascot body (rounded rect via arcs)
                moveTo(95f, 52f)
                lineTo(105f, 52f)
                arcToRelative(65f, 52f, 145f, 132f, 270f, 90f, false)
                lineTo(145f, 112f)
                arcToRelative(65f, 72f, 145f, 152f, 0f, 90f, false)
                lineTo(95f, 152f)
                arcToRelative(55f, 72f, 135f, 152f, 90f, 90f, false)
                lineTo(55f, 112f)
                arcToRelative(55f, 52f, 135f, 132f, 180f, 90f, false)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0D3B2E)),
                pathFillType = PathFillType.NonZero
            ) {
                // eyes
                addOval(Rect(72f, 86f, 92f, 106f))
                addOval(Rect(108f, 86f, 128f, 106f))
            }
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                // eye sparkles
                addOval(Rect(81.8f, 89.8f, 88.2f, 96.2f))
                addOval(Rect(117.8f, 89.8f, 124.2f, 96.2f))
            }
            path(
                fill = SolidColor(Color(0xFFC6F135)),
                pathFillType = PathFillType.NonZero
            ) {
                // lime belly arrow
                addRect(Rect(93f, 110f, 107f, 134f))
                moveTo(83f, 130f)
                lineTo(117f, 130f)
                lineTo(100f, 148f)
                close()
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null
