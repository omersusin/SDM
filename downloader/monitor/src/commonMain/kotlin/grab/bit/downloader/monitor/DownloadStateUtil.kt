package grab.bit.downloader.monitor

import grab.bit.downloader.downloaditem.DownloadJobStatus

fun IDownloadItemState.statusOrFinished(): DownloadJobStatus {
    return (this as? ProcessingDownloadItemState)?.status ?: DownloadJobStatus.Finished
}

fun IDownloadItemState.isFinished(): Boolean {
    return this is CompletedDownloadItemState
}
