package grab.bit.shared.util.ui

import androidx.compose.ui.graphics.vector.ImageVector
import grab.bit.util.compose.IIconResolver
import grab.bit.util.compose.IconSource
import grab.bit.util.compose.contants.ICON_PROTOCOL

abstract class BaseMyColors : IMyIcons, IIconResolver {
    val iconMap = mutableMapOf<String, IconSource>()
    fun ImageVector.asIconSource(
        name: String,
        requiredTint: Boolean = true,
    ): IconSource {
        val uri = "$ICON_PROTOCOL:$name"
        return IconSource.VectorIconSource(this, requiredTint, uri)
            .asIconSource()
    }

    fun IconSource.asIconSource(): IconSource = apply {
        uri?.let {
            iconMap[it] = this
        }
    }

    override fun resolve(uri: String): IconSource? {
        return iconMap[uri]
    }
}
