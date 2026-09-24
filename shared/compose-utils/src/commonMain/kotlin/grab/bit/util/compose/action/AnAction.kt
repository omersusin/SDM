package grab.bit.util.compose.action

import grab.bit.util.compose.IconSource
import grab.bit.util.compose.StringSource

abstract class AnAction(
    title: StringSource,
    icon: IconSource? = null,
) : MenuItem.SingleItem(
    title = title,
    icon = icon,
) {
    override fun onClick() = actionPerformed()

    abstract fun actionPerformed()
}


