import grab.bit.updateapplier.UpdateInstaller
import grab.bit.util.osfileutil.FileUtils
import java.io.File

class ApkInstaller(
    private val apkFile: File,
) : UpdateInstaller {
    override fun installUpdate() {
        FileUtils.openFile(apkFile)
    }
}
