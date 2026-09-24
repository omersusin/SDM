package grab.bit.shared.singledownloadpage

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.downloaditem.DownloadJobStatus
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.ProcessingDownloadItemState
import grab.bit.downloader.monitor.statusOrFinished
import grab.bit.downloader.utils.ExceptionUtils
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource

fun createStatusString(
    it: IDownloadItemState,
): StringSource {
    if (it is ProcessingDownloadItemState && it.isWaiting) {
        return Res.string.waiting.asStringSource()
    }
    return when (val status = it.statusOrFinished()) {
        is DownloadJobStatus.Canceled -> {
            if (ExceptionUtils.isNormalCancellation(status.e)) {
                Res.string.paused
            } else {
                Res.string.error
            }
        }

        DownloadJobStatus.Downloading -> Res.string.downloading
        DownloadJobStatus.Finished -> Res.string.finished
        DownloadJobStatus.IDLE -> Res.string.idle
        is DownloadJobStatus.PreparingFile -> Res.string.preparing_file
        DownloadJobStatus.Resuming -> Res.string.resuming
        is DownloadJobStatus.Retrying -> Res.string.retrying
    }.asStringSource()
}

fun createStatusStringWithReason(
    it: IDownloadItemState,
    errorReason: DownloadErrorReason?,
): StringSource {
    if (it is ProcessingDownloadItemState && it.isWaiting) {
        return Res.string.waiting.asStringSource()
    }
    return when (val status = it.statusOrFinished()) {
        is DownloadJobStatus.Canceled -> {
            if (ExceptionUtils.isNormalCancellation(status.e)) {
                Res.string.paused.asStringSource()
            } else {
                errorReason?.title?.asStringSource()
                    ?: Res.string.error.asStringSource()
            }
        }

        DownloadJobStatus.Downloading -> Res.string.downloading.asStringSource()
        DownloadJobStatus.Finished -> Res.string.finished.asStringSource()
        DownloadJobStatus.IDLE -> Res.string.idle.asStringSource()
        is DownloadJobStatus.PreparingFile -> Res.string.preparing_file.asStringSource()
        DownloadJobStatus.Resuming -> Res.string.resuming.asStringSource()
        is DownloadJobStatus.Retrying -> Res.string.retrying.asStringSource()
    }
}
