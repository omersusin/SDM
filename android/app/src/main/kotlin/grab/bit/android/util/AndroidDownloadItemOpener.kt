package grab.bit.android.util

import grab.bit.shared.util.DownloadItemOpener
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.util.osfileutil.FileUtils
import java.io.File

class AndroidDownloadItemOpener(
    private val downloadSystem: DownloadSystem
) : DownloadItemOpener {
    override suspend fun openDownloadItem(id: Long) {
        downloadSystem.getDownloadItemById(id)?.let {
            openDownloadItem(it)
        }
    }

    override suspend fun openDownloadItem(downloadItem: IDownloadItem) {
        try {
            FileUtils.openFile(File(downloadItem.folder, downloadItem.name))
        } catch (e: Exception) {
            // toast something
        }
    }

    override suspend fun openDownloadItemFolder(id: Long) {
        downloadSystem.getDownloadItemById(id)?.let {
            openDownloadItemFolder(it)
        }
    }

    override suspend fun openDownloadItemFolder(downloadItem: IDownloadItem) {
        try {
            FileUtils.openFolderOfFile(File(downloadItem.folder, downloadItem.name))
        } catch (e: Exception) {
            // toast something
        }
    }
}
