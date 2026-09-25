package grab.bit.resources.icons

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, 200f, 200f),
                        cornerRadius = CornerRadius(44f, 44f)
                    )
                )
            }
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                // mascot body
                moveTo(95f, 52f)
                lineTo(105f, 52f)
                arcTo(
                    rect = Rect(65f, 52f, 145f, 132f),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(145f, 112f)
                arcTo(
                    rect = Rect(65f, 72f, 145f, 152f),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(95f, 152f)
                arcTo(
                    rect = Rect(55f, 72f, 135f, 152f),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(55f, 92f)
                arcTo(
                    rect = Rect(55f, 52f, 135f, 132f),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                close()
                // belly arrow shaft
                addRoundRect(
                    RoundRect(
                        rect = Rect(93f, 110f, 107f, 134f),
                        cornerRadius = CornerRadius(7f, 7f)
                    )
                )
                // belly arrow head
                moveTo(83f, 130f)
                lineTo(117f, 130f)
                lineTo(100f, 148f)
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
                // lime arrow overlay on belly
                addRoundRect(
                    RoundRect(
                        rect = Rect(93f, 110f, 107f, 134f),
                        cornerRadius = CornerRadius(7f, 7f)
                    )
                )
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
