package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.downloaditem.IDownloadItem

interface OnDownloadCompletionActionProvider {
    suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction>
}

class NoOpOnDownloadCompletionActionProvider : OnDownloadCompletionActionProvider {
    override suspend fun getOnDownloadCompletionAction(downloadItem: IDownloadItem): List<OnDownloadCompletionAction> {
        return emptyList()
    }
}
