package grab.bit.android.pages.editdownload

import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.pages.editdownload.BaseEditDownloadComponent
import grab.bit.shared.util.mvi.ContainsEffects
import grab.bit.shared.util.mvi.supportEffects
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadItem
import kotlinx.coroutines.flow.*

class AndroidEditDownloadComponent(
    ctx: ComponentContext,
    onRequestClose: () -> Unit,
    downloadId: Long,
    acceptEdit: StateFlow<Boolean>,
    onEdited: ((IDownloadItem) -> Unit, DownloadJobExtraConfig?) -> Unit,
    downloadSystem: DownloadSystem,
    downloaderInUiRegistry: DownloaderInUiRegistry,
    iconProvider: FileIconProvider,
    downloadErrorDialogManager: DownloadErrorDialogManager,
) : BaseEditDownloadComponent(
    ctx = ctx,
    downloadSystem = downloadSystem,
    downloaderInUiRegistry = downloaderInUiRegistry,
    iconProvider = iconProvider,
    onEdited = onEdited,
    onRequestClose = onRequestClose,
    downloadId = downloadId,
    acceptEdit = acceptEdit,
    downloadErrorDialogManager = downloadErrorDialogManager,
),
    ContainsEffects<AndroidEditDownloadComponent.Effects> by supportEffects() {
    sealed interface Effects {
    }
}
