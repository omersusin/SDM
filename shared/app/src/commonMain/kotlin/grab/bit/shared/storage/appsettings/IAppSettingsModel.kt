package grab.bit.shared.storage.appsettings

import grab.bit.shared.storage.SupportedSizeUnits
import grab.bit.downloader.SpeedProfile
import grab.bit.util.config.datastore.SettingsTypeSafeSchema

expect class PlatformAppSettingsModel : IAppSettingsModel

expect val PlatformAppSettingsSchema: SettingsTypeSafeSchema<PlatformAppSettingsModel>

interface IAppSettingsModel {
    val theme: String
    val defaultDarkTheme: String
    val defaultLightTheme: String
    val language: String?
    val font: String?
    val uiScale: Float?
    val showIconLabels: Boolean
    val useRelativeDateTime: Boolean
    val threadCount: Int
    val maxConcurrentDownloads: Int
    val maxDownloadRetryCount: Int
    val retryDelaySeconds: Int
    val autoUncompressArchives: Boolean
    val clipboardMonitor: Boolean
    val autoRemoveFinishedDownloads: Boolean
    val clipboardAddPaused: Boolean
    val silentClipboardAdd: Boolean
    val dynamicPartCreation: Boolean
    val useServerLastModifiedTime: Boolean
    val appendExtensionToIncompleteDownloads: Boolean
    val useSparseFileAllocation: Boolean
    val useAverageSpeed: Boolean
    val showDownloadProgressDialog: Boolean
    val showDownloadCompletionDialog: Boolean
    val speedLimit: Long
    val autoStartOnBoot: Boolean
    val notificationSound: Boolean
    val generalNotificationSound: String
    val errorNotificationSound: String
    val successNotificationSound: String
    val defaultDownloadFolder: String
    val apiEnabled: Boolean
    val apiPort: Int
    val apiAuthKey: String
    val apiAuthEnabled: Boolean
    val trackDeletedFilesOnDisk: Boolean
    val deletePartialFileOnDownloadCancellation: Boolean
    val sizeUnit: SupportedSizeUnits
    val speedUnit: SupportedSizeUnits
    val ignoreSSLCertificates: Boolean
    val useCategoryByDefault: Boolean
    val organizeByType: Boolean
    val userAgent: String
    val speedProfile: SpeedProfile
}