package grab.bit.android.util.pagemanager

import android.content.Context
import android.content.Intent
import grab.bit.android.pages.downloaderror.DownloadErrorActivity
import grab.bit.shared.downloaderror.DownloadErrorComponent
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.serialization.json.Json

class AndroidDownloadErrorPageManager(
    private val openIntent: (Intent) -> Unit,
    private val context: Context,
    private val json: Json,
) : DownloadErrorDialogManager {
    override fun openDownloadErrorDialog(
        downloadItem: IDownloadItem,
        reason: DownloadErrorReason
    ) {
        openIntent(
            DownloadErrorActivity.createIntent(
                context,
                DownloadErrorComponent.DownloadErrorConfig(
                    downloadItem = downloadItem,
                    errorReason = reason,
                ),
                json = json,
            )
        )
    }

    override fun closeDownloadErrorDialog() {
        TODO("Not yet implemented")
    }
}
