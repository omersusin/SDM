package grab.bit

import java.io.File

fun interface UpdateDownloadLocationProvider {
    fun getSaveLocation(): File
}