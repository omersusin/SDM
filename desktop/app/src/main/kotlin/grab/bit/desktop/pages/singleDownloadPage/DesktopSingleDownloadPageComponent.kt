package grab.bit.desktop.pages.singleDownloadPage

import arrow.optics.copy
import grab.bit.desktop.storage.DesktopExtraDownloadItemSettings
import grab.bit.desktop.storage.PageStatesStorage
import grab.bit.resources.Res
import grab.bit.shared.pagemanager.DownloadErrorDialogManager
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.singledownloadpage.BaseSingleDownloadComponent
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.ExtraDownloadSettingsStorage
import grab.bit.shared.ui.configurable.item.BooleanConfigurable
import grab.bit.shared.util.*
import com.arkivanov.decompose.ComponentContext
import grab.bit.util.compose.asStringSource
import grab.bit.util.desktop.poweraction.PowerActionConfig
import grab.bit.util.flow.mapTwoWayStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import kotlin.getValue

class DesktopSingleDownloadComponent(
    ctx: ComponentContext,
    downloadItemOpener: DownloadItemOpener,
    downloadErrorDialogManager: DownloadErrorDialogManager,
    onDismiss: () -> Unit,
    downloadId: Long,
    extraDownloadSettingsStorage: ExtraDownloadSettingsStorage<DesktopExtraDownloadItemSettings>,
    downloadSystem: DownloadSystem,
    appSettings: BaseAppSettingsStorage,
    appRepository: BaseAppRepository,
    applicationScope: CoroutineScope,
    fileIconProvider: FileIconProvider,
) : BaseSingleDownloadComponent<DesktopExtraDownloadItemSettings>(
    ctx = ctx,
    downloadItemOpener = downloadItemOpener,
    downloadErrorDialogManager = downloadErrorDialogManager,
    onDismiss = onDismiss,
    downloadId = downloadId,
    extraDownloadSettingsStorage = extraDownloadSettingsStorage,
    downloadSystem = downloadSystem,
    appSettings = appSettings,
    appRepository = appRepository,
    applicationScope = applicationScope,
    fileIconProvider = fileIconProvider,
) {
    private val singleDownloadPageStateToPersist by lazy {
        get<PageStatesStorage>().singleDownloadPageState
    }
    override val defaultShowPartInfo: Boolean = singleDownloadPageStateToPersist.value.showPartInfo

    override fun setShowPartInfo(value: Boolean) {
        super.setShowPartInfo(value)
        singleDownloadPageStateToPersist.update {
            it.copy {
                SingleDownloadPageStateToPersist.showPartInfo.set(value)
            }
        }
    }

    sealed interface Effects : BaseSingleDownloadComponent.Effects.Platform {
        data object BringToFront : Effects
    }

    fun bringToFront() {
        sendEffect(Effects.BringToFront)
    }

    val onCompletion by lazy {
        listOf(
            BooleanConfigurable(
                title = Res.string.download_item_settings_shutdown_on_completion.asStringSource(),
                description = Res.string.download_item_settings_shutdown_on_completion_description.asStringSource(),
                backedBy = extraDownloadItemSettingsFlow.mapTwoWayStateFlow(
                    map = {
                        it.powerActionTypeOnFinish != null
                    },
                    unMap = {
                        copy(
                            powerActionTypeOnFinish = when (it) {
                                true -> PowerActionConfig.Type.Shutdown
                                false -> null
                            },
                        )
                    }
                ),
                describe = {
                    when (it) {
                        true -> Res.string.enabled
                        false -> Res.string.disabled
                    }.asStringSource()
                },
            ),
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


