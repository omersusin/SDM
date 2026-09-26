package grab.bit.android.pages.singledownload

import grab.bit.android.storage.AndroidExtraDownloadItemSettings
import grab.bit.resources.Res
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.singledownloadpage.BaseSingleDownloadComponent
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.ExtraDownloadSettingsStorage
import grab.bit.shared.ui.configurable.item.BooleanConfigurable
import grab.bit.shared.util.DownloadItemOpener
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FileIconProvider
import com.arkivanov.decompose.ComponentContext
import grab.bit.util.compose.asStringSource
import grab.bit.util.flow.mapTwoWayStateFlow
import kotlinx.coroutines.CoroutineScope

class AndroidSingleDownloadComponent(
    ctx: ComponentContext,
    downloadItemOpener: DownloadItemOpener,
    onDismiss: () -> Unit,
    downloadId: Long,
    extraDownloadSettingsStorage: ExtraDownloadSettingsStorage<AndroidExtraDownloadItemSettings>,
    downloadSystem: DownloadSystem,
    appSettings: BaseAppSettingsStorage,
    appRepository: BaseAppRepository,
    applicationScope: CoroutineScope,
    fileIconProvider: FileIconProvider,
    val comesFromExternalApplication: Boolean,
    downloadErrorDialogManager: DownloadErrorDialogManager,
) : BaseSingleDownloadComponent<AndroidExtraDownloadItemSettings>(
    ctx = ctx,
    downloadItemOpener = downloadItemOpener,
    onDismiss = onDismiss,
    downloadId = downloadId,
    extraDownloadSettingsStorage = extraDownloadSettingsStorage,
    downloadSystem = downloadSystem,
    appSettings = appSettings,
    appRepository = appRepository,
    applicationScope = applicationScope,
    fileIconProvider = fileIconProvider,
    downloadErrorDialogManager = downloadErrorDialogManager,
) {
    override val defaultShowPartInfo: Boolean = false

    sealed interface Effects : BaseSingleDownloadComponent.Effects.Platform

    val onCompletion by lazy {
        listOf(
            BooleanConfigurable(
                title = Res.string.download_item_settings_show_download_completion_dialog.asStringSource(),
                description = Res.string.download_item_settings_show_download_completion_dialog_description.asStringSource(),
                backedBy = itemShouldShowCompletionDialog.mapTwoWayStateFlow(
                    map = {
                        it ?: globalShowCompletionDialog.value
                    },
                    unMap = { it }
                ),
                describe = {
                    when (it) {
                        true -> Res.string.enabled
                        false -> Res.string.disabled
                    }.asStringSource()
                },
            ),
        )
    }

    data class Config(
        override val id: Long
    ) : BaseSingleDownloadComponent.Config
}


