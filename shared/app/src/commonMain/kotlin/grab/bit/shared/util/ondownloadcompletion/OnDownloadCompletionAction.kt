package grab.bit.shared.util.ondownloadcompletion

import grab.bit.downloader.downloaditem.IDownloadItem

interface OnDownloadCompletionAction {
    suspend fun onDownloadCompleted(downloadItem: IDownloadItem)
}
