package grab.bit.shared.util.downloadlocation

import grab.bit.shared.util.SystemDownloadLocationProvider

object PlatformDownloadLocationProvider {
    val instance: SystemDownloadLocationProvider by lazy {
        getPlatformDownloadLocationProvider()
    }
}

expect fun getPlatformDownloadLocationProvider(): SystemDownloadLocationProvider

