package grab.bit.desktop.actions.onevennts

import grab.bit.shared.storage.IExtraDownloadSettingsStorage
import grab.bit.shared.util.ondownloadcompletion.OnDownloadCompletionAction
import grab.bit.downloader.downloaditem.IDownloadItem

class CleanExtraSettingsOnDownloadFinish(
    private val storage: IExtraDownloadSettingsStorage<*>
) : OnDownloadCompletionAction {
    override suspend fun onDownloadCompleted(downloadItem: IDownloadItem) {
        storage.deleteExtraDownloadItemSettings(downloadItem.id)
    }
}
