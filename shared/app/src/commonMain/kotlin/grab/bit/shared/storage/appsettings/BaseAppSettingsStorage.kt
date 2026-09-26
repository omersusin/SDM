package grab.bit.shared.storage.appsettings

import grab.bit.shared.storage.SupportedSizeUnits
import grab.bit.shared.ui.theme.ThemeSettingsStorage
import grab.bit.shared.util.notification.INotificationSettingsStorage
import grab.bit.downloader.SpeedProfile
import grab.bit.util.compose.localizationmanager.LanguageStorage
import kotlinx.coroutines.flow.MutableStateFlow


interface BaseAppSettingsStorage :
    LanguageStorage,
    ThemeSettingsStorage,
    INotificationSettingsStorage {
    override val theme: MutableStateFlow<String>
    override val defaultDarkTheme: MutableStateFlow<String>
    override val defaultLightTheme: MutableStateFlow<String>
    override val selectedLanguage: MutableStateFlow<String?>
    val font: MutableStateFlow<String?>
    val uiScale: MutableStateFlow<Float>
    val showIconLabels: MutableStateFlow<Boolean>
    val useRelativeDateTime: MutableStateFlow<Boolean>
    val threadCount: MutableStateFlow<Int>
    val maxConcurrentDownloads: MutableStateFlow<Int>
    val dynamicPartCreation: MutableStateFlow<Boolean>
    val useServerLastModifiedTime: MutableStateFlow<Boolean>
    val appendExtensionToIncompleteDownloads: MutableStateFlow<Boolean>
    val useSparseFileAllocation: MutableStateFlow<Boolean>
    val useAverageSpeed: MutableStateFlow<Boolean>
    val maxDownloadRetryCount: MutableStateFlow<Int>
    val retryDelaySeconds: MutableStateFlow<Int>
    val maxConnectionsPerHost: MutableStateFlow<Int>
    val interDownloadDelayMs: MutableStateFlow<Int>
    val minSplitSizeKb: MutableStateFlow<Int>
    val httpTimeoutSeconds: MutableStateFlow<Int>
    val autoUncompressArchives: MutableStateFlow<Boolean>
    val clipboardMonitor: MutableStateFlow<Boolean>
    val autoRemoveFinishedDownloads: MutableStateFlow<Boolean>
    val copyFinishedTo: MutableStateFlow<String>
    val clipboardAddPaused: MutableStateFlow<Boolean>
    val silentClipboardAdd: MutableStateFlow<Boolean>
    val showDownloadProgressDialog: MutableStateFlow<Boolean>
    val showDownloadCompletionDialog: MutableStateFlow<Boolean>
    val completionDialogOnErrorOnly: MutableStateFlow<Boolean>
    val autoDismissFinishedNotification: MutableStateFlow<Boolean>
    val compactCompletionNotification: MutableStateFlow<Boolean>
    val speedLimit: MutableStateFlow<Long>
    val autoStartOnBoot: MutableStateFlow<Boolean>
    override val notificationSound: MutableStateFlow<Boolean>
    override val generalNotificationSound: MutableStateFlow<String>
    override val errorNotificationSound: MutableStateFlow<String>
    override val successNotificationSound: MutableStateFlow<String>
    val defaultDownloadFolder: MutableStateFlow<String>
    val apiEnabled: MutableStateFlow<Boolean>
    val apiPort: MutableStateFlow<Int>
    val apiAuthEnabled: MutableStateFlow<Boolean>
    val apiAuthKey: MutableStateFlow<String>
    val webhookUrl: MutableStateFlow<String>
    val trackDeletedFilesOnDisk: MutableStateFlow<Boolean>
    val deletePartialFileOnDownloadCancellation: MutableStateFlow<Boolean>
    val sizeUnit: MutableStateFlow<SupportedSizeUnits>
    val speedUnit: MutableStateFlow<SupportedSizeUnits>
    val ignoreSSLCertificates: MutableStateFlow<Boolean>
    val useCategoryByDefault: MutableStateFlow<Boolean>
    val organizeByType: MutableStateFlow<Boolean>
    val userAgent: MutableStateFlow<String>
    val speedProfile: MutableStateFlow<SpeedProfile>
    val captureBlockedExtensions: MutableStateFlow<String>
}
