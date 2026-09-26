package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.downloader.downloaditem.contexts.RemovedBy
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.autoremove.AutoRemoveOption
import kotlinx.coroutines.flow.StateFlow

class AutoRemoveOnCompletionAction(
    private val downloadSystem: () -> DownloadSystem,
) : OnDownloadCompletionAction {
    override suspend fun onDownloadCompleted(downloadItem: IDownloadItem) {
        runCatching {
            downloadSystem().removeDownload(
                id = downloadItem.id,
                alsoRemoveFile = false,
                context = RemovedBy(AutoRemoveOption),
            )
        }
    }
}

class AutoRemoveOnCompletionProvider(
    private val enabled: StateFlow<Boolean>,
    downloadSystem: () -> DownloadSystem,
) : OnDownloadCompletionActionProvider {
    private val action by lazy { AutoRemoveOnCompletionAction(downloadSystem) }

    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        if (!enabled.value) {
            return emptyList()
        }
        return listOf(action)
    }
}

class CompositeOnDownloadCompletionProvider(
    private val providers: List<OnDownloadCompletionActionProvider>,
) : OnDownloadCompletionActionProvider {
    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        return providers.flatMap { it.getOnDownloadCompletionAction(downloadItem) }
    }
}
