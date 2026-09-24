import grab.bit.InstallableArch
import grab.bit.updateapplier.BaseUpdateApplier
import grab.bit.updateapplier.UpdateDownloader
import grab.bit.updateapplier.UpdateInstaller
import grab.bit.updateapplier.UpdatePreparer
import grab.bit.updatechecker.UpdateInfo
import grab.bit.updatechecker.UpdateSource

/**
 * this update applier works for direct downloads!
 */
class AndroidDirectLinkUpdateApplier(
    private val updateDownloader: UpdateDownloader,
) : BaseUpdateApplier() {
    override fun updateSupported(): Boolean {
        return true
    }

    override fun getUpdatePreparer(): UpdatePreparer {
        return updateDownloader
    }

    override fun getBestDownloadSource(updateInfo: UpdateInfo): UpdateSource {
        val downloadableSources =
            updateInfo.updateSource.filterIsInstance<UpdateSource.DirectDownloadLink>()
                .sortedBy {
                    // universal downloads have bigger size so we put them last
                    it.installableArch !is InstallableArch.Universal
                }
        val downloadSource = downloadableSources.find {
            isApk(it.name)
        }
        return requireNotNull(downloadSource) {
            "Can't find proper download link for your platform! Please update it manually"
        }
    }

    override fun getUpdateInstaller(preparedUpdate: UpdatePreparer.PreparedUpdate): UpdateInstaller {
        return ApkInstaller((preparedUpdate as UpdateDownloader.PreparedUpdateFile).file)
    }

    fun isApk(name: String): Boolean {
        return name.endsWith(".apk")
    }
}
