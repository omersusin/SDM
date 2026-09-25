package grab.bit.resources.icons

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
                moveTo(0f, 0f)
                lineTo(200f, 0f)
                lineTo(200f, 200f)
                lineTo(0f, 200f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {

                moveTo(95.0f, 52.0f)
                lineTo(105.0f, 52.0f)
                curveTo(127.1f, 52.0f, 145.0f, 69.9f, 145.0f, 92.0f)
                lineTo(145.0f, 112.0f)
                curveTo(145.0f, 134.1f, 127.1f, 152.0f, 105.0f, 152.0f)
                lineTo(95.0f, 152.0f)
                curveTo(72.9f, 152.0f, 55.0f, 134.1f, 55.0f, 112.0f)
                lineTo(55.0f, 92.0f)
                curveTo(55.0f, 69.9f, 72.9f, 52.0f, 95.0f, 52.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0D3B2E)),
                pathFillType = PathFillType.NonZero
            ) {

                moveTo(92.0f, 96.0f)
                curveTo(92.0f, 101.5f, 87.5f, 106.0f, 82.0f, 106.0f)
                curveTo(76.5f, 106.0f, 72.0f, 101.5f, 72.0f, 96.0f)
                curveTo(72.0f, 90.5f, 76.5f, 86.0f, 82.0f, 86.0f)
                curveTo(87.5f, 86.0f, 92.0f, 90.5f, 92.0f, 96.0f)
                close()
                moveTo(128.0f, 96.0f)
                curveTo(128.0f, 101.5f, 123.5f, 106.0f, 118.0f, 106.0f)
                curveTo(112.5f, 106.0f, 108.0f, 101.5f, 108.0f, 96.0f)
                curveTo(108.0f, 90.5f, 112.5f, 86.0f, 118.0f, 86.0f)
                curveTo(123.5f, 86.0f, 128.0f, 90.5f, 128.0f, 96.0f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {

                moveTo(88.2f, 93.0f)
                curveTo(88.2f, 94.8f, 86.8f, 96.2f, 85.0f, 96.2f)
                curveTo(83.2f, 96.2f, 81.8f, 94.8f, 81.8f, 93.0f)
                curveTo(81.8f, 91.2f, 83.2f, 89.8f, 85.0f, 89.8f)
                curveTo(86.8f, 89.8f, 88.2f, 91.2f, 88.2f, 93.0f)
                close()
                moveTo(124.2f, 93.0f)
                curveTo(124.2f, 94.8f, 122.8f, 96.2f, 121.0f, 96.2f)
                curveTo(119.2f, 96.2f, 117.8f, 94.8f, 117.8f, 93.0f)
                curveTo(117.8f, 91.2f, 119.2f, 89.8f, 121.0f, 89.8f)
                curveTo(122.8f, 89.8f, 124.2f, 91.2f, 124.2f, 93.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC6F135)),
                pathFillType = PathFillType.NonZero
            ) {

                moveTo(100.0f, 110.0f)
                lineTo(100.0f, 110.0f)
                curveTo(103.9f, 110.0f, 107.0f, 113.1f, 107.0f, 117.0f)
                lineTo(107.0f, 127.0f)
                curveTo(107.0f, 130.9f, 103.9f, 134.0f, 100.0f, 134.0f)
                lineTo(100.0f, 134.0f)
                curveTo(96.1f, 134.0f, 93.0f, 130.9f, 93.0f, 127.0f)
                lineTo(93.0f, 117.0f)
                curveTo(93.0f, 113.1f, 96.1f, 110.0f, 100.0f, 110.0f)
                close()
                moveTo(83f, 130f)
                lineTo(117f, 130f)
                lineTo(100f, 148f)
                close()
