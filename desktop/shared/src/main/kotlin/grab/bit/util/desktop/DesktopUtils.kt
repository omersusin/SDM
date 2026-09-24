package grab.bit.util.desktop

import grab.bit.util.desktop.poweraction.PowerAction
import grab.bit.util.desktop.utils.linux.LinuxUtils
import grab.bit.util.desktop.utils.mac.MacOSUtils
import grab.bit.util.desktop.utils.windows.WindowsUtils
import grab.bit.util.platform.Platform


interface DesktopUtils {
    fun openSystemProxySettings()
    fun powerAction(): PowerAction

    companion object : DesktopUtils by getDesktopUtilOfCurrentOS()
}

private fun getDesktopUtilOfCurrentOS(): DesktopUtils {
    val platform = Platform.getCurrentPlatform() as Platform.Desktop
    return when (platform) {
        Platform.Desktop.Windows -> WindowsUtils()
        Platform.Desktop.MacOS -> MacOSUtils()
        Platform.Desktop.Linux -> LinuxUtils()
    }
}

