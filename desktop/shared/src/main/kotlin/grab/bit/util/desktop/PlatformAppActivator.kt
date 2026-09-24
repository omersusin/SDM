package grab.bit.util.desktop

import grab.bit.util.desktop.activator.mac.MacAppActivator
import grab.bit.util.platform.Platform

interface PlatformAppActivator {
    fun active()

    companion object : PlatformAppActivator by getPlatformAppActivatorForCurrentOs()
}

class EmptyAppActivator : PlatformAppActivator {
    override fun active() {
        // no-op
    }
}

private fun getPlatformAppActivatorForCurrentOs() = when (Platform.getCurrentPlatform()) {
    Platform.Desktop.MacOS -> MacAppActivator()
    else -> EmptyAppActivator()
}