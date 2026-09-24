package grab.bit.shared.updater

import grab.bit.UpdateDownloadLocationProvider
import grab.bit.shared.util.DownloadSystem
import grab.bit.updateapplier.UpdateDownloader
import grab.bit.updatechecker.UpdateSource
import grab.bit.downloader.NewDownloadItemProps
import grab.bit.downloader.downloaditem.EmptyContext
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import grab.bit.downloader.utils.OnDuplicateStrategy
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

class UpdateDownloaderViaDownloadSystem(
    private val downloadSystem: DownloadSystem,
    private val updateDownloadLocationProvider: UpdateDownloadLocationProvider,
) : UpdateDownloader() {
    override suspend fun downloadUpdateFile(updateDirectDownloadLink: UpdateSource.DirectDownloadLink): File {
        val updateDownloadsFolder = updateDownloadLocationProvider.getSaveLocation().path
        val updateDownloads = downloadSystem.getDownloadItemsByFolder(updateDownloadsFolder)
        val pausedDownload = updateDownloads.find {
            it.name == updateDirectDownloadLink.name
        }
        // at the moment if the download was finished but removed from the filesystem
        // download will not be restarted automatically
        val requireRestartDownload = pausedDownload?.getFullPath()?.exists()?.not() ?: false
        val id = pausedDownload?.id
            ?: downloadSystem.addDownload(
                newDownload = NewDownloadItemProps(
                    downloadItem = HttpDownloadItem(
                        id = -1,
                        link = updateDirectDownloadLink.link,
                        folder = updateDownloadsFolder,
                        name = updateDirectDownloadLink.name,
                    ),
                    onDuplicateStrategy = OnDuplicateStrategy.AddNumbered,
                    extraConfig = null,
                    context = EmptyContext,
                ),
                queueId = null,
                categoryId = null,
            )
        coroutineScope {
            if (requireRestartDownload) {
                downloadSystem.reset(id)
            }
            val waiter = async {
                downloadSystem.downloadMonitor.waitForDownloadToFinishOrCancel(id)
            }
            downloadSystem.manualResume(id, EmptyContext)
            waiter.await()
        }
        // we recheck download info maybe some dude change the file name!
        val downloadedItem = downloadSystem.getDownloadItemById(id)
        requireNotNull(downloadedItem) {
            "Download is removed!"
        }
        return downloadSystem.getDownloadFile(downloadedItem)
    }

    override suspend fun removeUpdateFiles(updateDirectDownloadLink: UpdateSource.DirectDownloadLink) {
        val id = downloadSystem
            .getDownloadItemsByFolder(updateDownloadLocationProvider.getSaveLocation().path)
            .find { it.name == updateDirectDownloadLink.name }?.id
        id?.let {
            downloadSystem.removeDownload(id, true, EmptyContext)
        }
    }

    override suspend fun removeAllUpdateFiles() {
        val ids = downloadSystem
            .getDownloadItemsByFolder(updateDownloadLocationProvider.getSaveLocation().path)
            .map { it.id }
        for (id in ids) {
            downloadSystem.removeDownload(
                id = id, alsoRemoveFile = true, EmptyContext
            )
        }
    }
}
