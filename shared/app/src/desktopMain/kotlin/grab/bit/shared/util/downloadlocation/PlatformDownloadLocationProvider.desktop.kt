package grab.bit.shared.util.downloadlocation

import grab.bit.shared.util.SystemDownloadLocationProvider
import grab.bit.util.platform.Platform
import grab.bit.util.platform.asDesktop

actual fun getPlatformDownloadLocationProvider(): SystemDownloadLocationProvider {
    return when (Platform.asDesktop()) {
        Platform.Desktop.Windows -> WindowsDownloadLocationProvider()
        Platform.Desktop.Linux -> LinuxDownloadLocationProvider()
        Platform.Desktop.MacOS -> MacDownloadLocationProvider()
    }
}
