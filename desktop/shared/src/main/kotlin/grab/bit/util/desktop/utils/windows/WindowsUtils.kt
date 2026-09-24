package grab.bit.util.desktop.utils.windows

import grab.bit.util.desktop.DesktopUtils
import grab.bit.util.desktop.poweraction.PowerAction
import grab.bit.util.desktop.poweraction.PowerActionWindows
import grab.bit.util.execAndWait

class WindowsUtils : DesktopUtils {
    private val powerActionWindows = PowerActionWindows()
    override fun openSystemProxySettings() {
        val result = execAndWait(
            arrayOf(
                "cmd", "/c", "start",
                "ms-settings:network-proxy",
            )
        )
        if (!result) {
            execAndWait(
                arrayOf(
                    "rundll32.exe shell32.dll,Control_RunDLL inetcpl.cpl,,4"
                )
            )
        }
    }

    override fun powerAction(): PowerAction {
        return powerActionWindows
    }
}
