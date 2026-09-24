package grab.bit.shared.util.keepawake

import grab.bit.util.platform.Platform
import grab.bit.util.platform.asDesktop

private val instance by lazy {
    when (Platform.asDesktop()) {
        Platform.Desktop.Windows -> WindowsKeepAwake()
        Platform.Desktop.MacOS -> MacKeepAwake()
        Platform.Desktop.Linux -> KeepAwake.NoOpKeepAwake()
    }
}

actual fun platformKeepAwake(): KeepAwake {
    return instance
}
