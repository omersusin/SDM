package grab.bit.shared.pagemanager

import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.downloaditem.IDownloadItem

interface DownloadErrorDialogManager {
    fun openDownloadErrorDialog(downloadItem: IDownloadItem, reason: DownloadErrorReason)
    fun closeDownloadErrorDialog()
}
