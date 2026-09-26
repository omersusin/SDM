package grab.bit.android.storage

import androidx.datastore.core.DataStore
import arrow.optics.Lens
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.storage.appsettings.AppSettingsModel
import grab.bit.shared.storage.appsettings.*
import grab.bit.shared.util.ConfigBaseSettingsByJson
import grab.bit.shared.util.ui.theme.DEFAULT_UI_SCALE
import grab.bit.util.VideoQualities


private val fontLens: Lens<AppSettingsModel, String?>
    get() = Lens(
        get = {
            it.font
        },
        set = { s, f ->
            s.copy(font = f)
        }
    )

// use null for default scale!
private val uiScaleLens: Lens<AppSettingsModel, Float>
    get() = Lens(
        get = {
            it.uiScale ?: DEFAULT_UI_SCALE
        },
        set = { s, f ->
            s.copy(uiScale = f.takeIf { it != DEFAULT_UI_SCALE })
        }
    )
private val languageLens: Lens<AppSettingsModel, String?>
    get() = Lens(
        get = {
            it.language
        },
        set = { s, f ->
            s.copy(language = f)
        }
    )

class AppSettingsStorage(
    settings: DataStore<AppSettingsModel>,
) : BaseAppSettingsStorage,
    ConfigBaseSettingsByJson<AppSettingsModel>(settings) {
    override val theme = from(AppSettingsModel.theme)
    override val defaultDarkTheme = from(AppSettingsModel.defaultDarkTheme)
    override val defaultLightTheme = from(AppSettingsModel.defaultLightTheme)

    override val selectedLanguage = from(languageLens)
    override val font = from(fontLens)
    override val uiScale = from(uiScaleLens)
    override val showIconLabels = from(AppSettingsModel.showIconLabels)
    override val useRelativeDateTime = from(AppSettingsModel.useRelativeDateTime)
    override val threadCount = from(AppSettingsModel.threadCount)
    override val maxConcurrentDownloads = from(AppSettingsModel.maxConcurrentDownloads)
    override val dynamicPartCreation = from(AppSettingsModel.dynamicPartCreation)
    override val useServerLastModifiedTime = from(AppSettingsModel.useServerLastModifiedTime)
    override val appendExtensionToIncompleteDownloads = from(AppSettingsModel.appendExtensionToIncompleteDownloads)
    override val useSparseFileAllocation = from(AppSettingsModel.useSparseFileAllocation)
    override val useAverageSpeed = from(AppSettingsModel.useAverageSpeed)
    override val maxDownloadRetryCount = from(AppSettingsModel.maxDownloadRetryCount)
    override val retryDelaySeconds = from(AppSettingsModel.retryDelaySeconds)
    override val maxConnectionsPerHost = from(AppSettingsModel.maxConnectionsPerHost)
    override val interDownloadDelayMs = from(AppSettingsModel.interDownloadDelayMs)
    override val minSplitSizeKb = from(AppSettingsModel.minSplitSizeKb)
    override val httpTimeoutSeconds = from(AppSettingsModel.httpTimeoutSeconds)
    override val autoUncompressArchives = from(AppSettingsModel.autoUncompressArchives)
    override val clipboardMonitor = from(AppSettingsModel.clipboardMonitor)
    override val autoRemoveFinishedDownloads = from(AppSettingsModel.autoRemoveFinishedDownloads)
    override val clipboardAddPaused = from(AppSettingsModel.clipboardAddPaused)
    override val silentClipboardAdd = from(AppSettingsModel.silentClipboardAdd)
    override val showDownloadProgressDialog = from(AppSettingsModel.showDownloadProgressDialog)
    override val showDownloadCompletionDialog = from(AppSettingsModel.showDownloadCompletionDialog)
    override val speedLimit = from(AppSettingsModel.speedLimit)
    override val autoStartOnBoot = from(AppSettingsModel.autoStartOnBoot)
    override val notificationSound = from(AppSettingsModel.notificationSound)
    override val generalNotificationSound = from(AppSettingsModel.generalNotificationSound)
    override val errorNotificationSound = from(AppSettingsModel.errorNotificationSound)
    override val successNotificationSound = from(AppSettingsModel.successNotificationSound)
    override val defaultDownloadFolder = from(AppSettingsModel.defaultDownloadFolder)
    override val apiEnabled = from(AppSettingsModel.apiEnabled)
    override val apiPort = from(AppSettingsModel.apiPort)
    override val apiAuthEnabled = from(AppSettingsModel.apiAuthEnabled)
    override val apiAuthKey = from(AppSettingsModel.apiAuthKey)
    override val trackDeletedFilesOnDisk = from(AppSettingsModel.trackDeletedFilesOnDisk)
    override val deletePartialFileOnDownloadCancellation =
        from(AppSettingsModel.deletePartialFileOnDownloadCancellation)
    override val sizeUnit = from(AppSettingsModel.sizeUnit)
    override val speedUnit = from(AppSettingsModel.speedUnit)
    override val ignoreSSLCertificates = from(AppSettingsModel.ignoreSSLCertificates)
    override val useCategoryByDefault = from(AppSettingsModel.useCategoryByDefault)
    override val userAgent = from(AppSettingsModel.userAgent)
    override val speedProfile = from(AppSettingsModel.speedProfile)

    val browserIconInLauncher = from(AppSettingsModel.browserIconInLauncher)
    val grabberUiMode = from(AppSettingsModel.grabberUiMode)
    val adBlockEnabled = from(AppSettingsModel.adBlockEnabled)
    val wifiOnlyDownloads = from(AppSettingsModel.wifiOnlyDownloads)
    val videoMaxHeight = from(AppSettingsModel.videoMaxHeight)
    val videoQuality = from(AppSettingsModel.videoQuality)
    private val videoQualityMigrated = from(AppSettingsModel.videoQualityMigrated)

    fun ensureVideoQualityMigrated() {
        if (videoQualityMigrated.value) return
        videoQuality.value = VideoQualities.migrateStoredHeight(videoMaxHeight.value)
        videoQualityMigrated.value = true
    }
}
