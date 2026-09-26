package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.DownloadManager
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class CopyOnCompletionAction(
    private val targetFolder: () -> String,
    private val downloadManager: () -> DownloadManager,
) : OnDownloadCompletionAction {
    override suspend fun onDownloadCompleted(downloadItem: IDownloadItem) {
        val destDir = targetFolder().takeIf { it.isNotBlank() } ?: return
        val src = runCatching { downloadManager().calculateOutputFile(downloadItem) }.getOrNull()
            ?: return
        if (!src.isFile) {
            return
        }
        runCatching {
            val dest = File(destDir)
            if (!dest.isDirectory && !dest.mkdirs()) {
                return
            }
            src.copyTo(File(dest, src.name), overwrite = true)
        }
    }
}

class CopyOnCompletionProvider(
    private val targetFolder: StateFlow<String>,
    downloadManager: () -> DownloadManager,
) : OnDownloadCompletionActionProvider {
    private val action by lazy { CopyOnCompletionAction({ targetFolder.value }, downloadManager) }

    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        if (targetFolder.value.isBlank()) {
            return emptyList()
        }
        return listOf(action)
    }
}
