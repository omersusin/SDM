package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.shared.util.archive.uncompressArchive
import kotlinx.coroutines.flow.StateFlow

class UncompressOnCompletionAction : OnDownloadCompletionAction {
    override suspend fun onDownloadCompleted(downloadItem: IDownloadItem) {
        if (!downloadItem.name.endsWith(".zip", ignoreCase = true)) {
            return
        }
        val archivePath = downloadItem.folder.trimEnd('/') + "/" + downloadItem.name
        val destDir = archivePath.dropLast(".zip".length)
        runCatching {
            uncompressArchive(archivePath, destDir)
        }
    }
}

class UncompressOnCompletionProvider(
    private val enabled: StateFlow<Boolean>,
) : OnDownloadCompletionActionProvider {
    private val action = UncompressOnCompletionAction()

    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        if (!enabled.value) {
            return emptyList()
        }
        return listOf(action)
    }
}
