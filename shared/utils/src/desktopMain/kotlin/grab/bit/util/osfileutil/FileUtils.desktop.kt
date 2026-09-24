package grab.bit.util.osfileutil

import grab.bit.util.platform.Platform
import grab.bit.util.platform.asDesktop

actual fun getPlatformFileUtil(): FileUtils {
    return when (Platform.asDesktop()) {
        Platform.Desktop.Windows -> WindowsFileUtils()
        Platform.Desktop.Linux -> LinuxFileUtils()
        Platform.Desktop.MacOS -> MacOsFileUtils()
    }
}
