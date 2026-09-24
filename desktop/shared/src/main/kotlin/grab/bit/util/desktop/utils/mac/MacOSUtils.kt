package grab.bit.util.desktop.utils.mac

import grab.bit.util.desktop.DesktopUtils
import grab.bit.util.desktop.poweraction.PowerAction
import grab.bit.util.desktop.poweraction.PowerActionMac
import grab.bit.util.execAndWait

class MacOSUtils : DesktopUtils {
    private val powerActionForMac = PowerActionMac()
    override fun openSystemProxySettings() {
        val commands = listOf(
            arrayOf("open", "x-apple.systempreferences:com.apple.Network-Settings.extension"),
            arrayOf("open", "/System/Library/PreferencePanes/Network.prefPane"),
            arrayOf("open", "/System/Preferences/Network")
        )

        for (command in commands) {
            if (execAndWait(command)) return
        }
    }

    override fun powerAction(): PowerAction {
        return powerActionForMac
    }
}
