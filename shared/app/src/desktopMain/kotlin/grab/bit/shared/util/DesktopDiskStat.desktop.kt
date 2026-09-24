package grab.bit.shared.util

import grab.bit.downloader.utils.IDiskStat
import java.io.File

actual typealias PlatformDiskStat = DesktopDiskStat

class DesktopDiskStat : IDiskStat {
    override fun getRemainingSpace(path: File): Long {
        return path.freeSpace
    }
}
