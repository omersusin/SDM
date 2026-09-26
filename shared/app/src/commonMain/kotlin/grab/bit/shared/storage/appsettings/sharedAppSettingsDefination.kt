package grab.bit.shared.storage.appsettings

import grab.bit.shared.storage.SupportedSizeUnits
import grab.bit.shared.util.MaximumDownloadRetriesLimitation
import grab.bit.shared.util.schemakt.enum
import grab.bit.downloader.SpeedProfile
import io.github.amir1376.schemakt.S
import io.github.amir1376.schemakt.schema.composite.TypeSafeObjectSchemaBuilder
import io.github.amir1376.schemakt.schema.modifier.catch
import io.github.amir1376.schemakt.schema.modifier.min
import io.github.amir1376.schemakt.schema.modifier.nullable
import io.github.amir1376.schemakt.schema.modifier.range
import io.github.amir1376.schemakt.schema.primitive.boolean
import io.github.amir1376.schemakt.schema.primitive.float
import io.github.amir1376.schemakt.schema.primitive.int
import io.github.amir1376.schemakt.schema.primitive.long
import io.github.amir1376.schemakt.schema.primitive.string

object BaseAppSettingsDefinition {
    context(builder: TypeSafeObjectSchemaBuilder<PlatformAppSettingsModel>)
    fun sharedAppSettingsTypeSafeDefinition() = builder.run {
        prop(IAppSettingsModel::theme) bind S.string().catch(PlatformDefaultSettings::theme)
        prop(IAppSettingsModel::defaultDarkTheme) bind S.string().catch(PlatformDefaultSettings::defaultDarkTheme)
        prop(IAppSettingsModel::defaultLightTheme) bind S.string().catch(PlatformDefaultSettings::defaultLightTheme)
        prop(IAppSettingsModel::language) bind S.string().nullable().catch(PlatformDefaultSettings::language)
        prop(IAppSettingsModel::font) bind S.string().nullable().catch(PlatformDefaultSettings::font)
        prop(IAppSettingsModel::uiScale) bind S.float().nullable().catch(PlatformDefaultSettings::uiScale)
        prop(IAppSettingsModel::showIconLabels) bind S.boolean().catch(PlatformDefaultSettings::showIconLabels)
        prop(IAppSettingsModel::useRelativeDateTime) bind S.boolean()
            .catch(PlatformDefaultSettings::useRelativeDateTime)
        prop(IAppSettingsModel::threadCount) bind S.int().catch(PlatformDefaultSettings::threadCount)
        prop(IAppSettingsModel::maxConcurrentDownloads) bind S.int()
            .range(0, MaximumDownloadRetriesLimitation.MAX_ALLOWED_RETRIES)
            .catch(PlatformDefaultSettings::maxConcurrentDownloads)
        prop(IAppSettingsModel::maxDownloadRetryCount) bind S.int()
            .range(0, MaximumDownloadRetriesLimitation.MAX_ALLOWED_RETRIES).catch(
                PlatformDefaultSettings::maxDownloadRetryCount
            )
        prop(IAppSettingsModel::retryDelaySeconds) bind S.int()
            .range(0, 3600).catch(
                PlatformDefaultSettings::retryDelaySeconds
            )
        prop(IAppSettingsModel::maxConnectionsPerHost) bind S.int()
            .range(0, 32).catch(
                PlatformDefaultSettings::maxConnectionsPerHost
            )
        prop(IAppSettingsModel::interDownloadDelayMs) bind S.int()
            .range(0, 60000).catch(
                PlatformDefaultSettings::interDownloadDelayMs
            )
        prop(IAppSettingsModel::minSplitSizeKb) bind S.int()
            .range(64, 1024 * 1024).catch(
                PlatformDefaultSettings::minSplitSizeKb
            )
        prop(IAppSettingsModel::httpTimeoutSeconds) bind S.int()
            .range(5, 300).catch(
                PlatformDefaultSettings::httpTimeoutSeconds
            )
        prop(IAppSettingsModel::autoUncompressArchives) bind S.boolean()
            .catch(PlatformDefaultSettings::autoUncompressArchives)
        prop(IAppSettingsModel::clipboardMonitor) bind S.boolean()
            .catch(PlatformDefaultSettings::clipboardMonitor)
        prop(IAppSettingsModel::autoRemoveFinishedDownloads) bind S.boolean()
            .catch(PlatformDefaultSettings::autoRemoveFinishedDownloads)
        prop(IAppSettingsModel::copyFinishedTo) bind S.string()
            .catch(PlatformDefaultSettings::copyFinishedTo)
        prop(IAppSettingsModel::clipboardAddPaused) bind S.boolean()
            .catch(PlatformDefaultSettings::clipboardAddPaused)
        prop(IAppSettingsModel::silentClipboardAdd) bind S.boolean()
            .catch(PlatformDefaultSettings::silentClipboardAdd)
        prop(IAppSettingsModel::dynamicPartCreation) bind S.boolean()
            .catch(PlatformDefaultSettings::dynamicPartCreation)
        prop(IAppSettingsModel::useServerLastModifiedTime) bind S.boolean()
            .catch(PlatformDefaultSettings::useServerLastModifiedTime)
        prop(IAppSettingsModel::appendExtensionToIncompleteDownloads) bind S.boolean()
            .catch(PlatformDefaultSettings::appendExtensionToIncompleteDownloads)
        prop(IAppSettingsModel::useSparseFileAllocation) bind S.boolean()
            .catch(PlatformDefaultSettings::useSparseFileAllocation)
        prop(IAppSettingsModel::useAverageSpeed) bind S.boolean().catch(PlatformDefaultSettings::useAverageSpeed)
        prop(IAppSettingsModel::showDownloadProgressDialog) bind S.boolean()
            .catch(PlatformDefaultSettings::showDownloadProgressDialog)
        prop(IAppSettingsModel::showDownloadCompletionDialog) bind S.boolean()
            .catch(PlatformDefaultSettings::showDownloadCompletionDialog)
        prop(IAppSettingsModel::completionDialogOnErrorOnly) bind S.boolean()
            .catch(PlatformDefaultSettings::completionDialogOnErrorOnly)
        prop(IAppSettingsModel::autoDismissFinishedNotification) bind S.boolean()
            .catch(PlatformDefaultSettings::autoDismissFinishedNotification)
        prop(IAppSettingsModel::compactCompletionNotification) bind S.boolean()
            .catch(PlatformDefaultSettings::compactCompletionNotification)
        prop(IAppSettingsModel::speedLimit) bind S.long().min(0L).catch(PlatformDefaultSettings::speedLimit)
        prop(IAppSettingsModel::autoStartOnBoot) bind S.boolean().catch(PlatformDefaultSettings::autoStartOnBoot)
        prop(IAppSettingsModel::notificationSound) bind S.boolean().catch(PlatformDefaultSettings::notificationSound)
        prop(IAppSettingsModel::generalNotificationSound) bind S.string()
            .catch(PlatformDefaultSettings::generalNotificationSound)
        prop(IAppSettingsModel::errorNotificationSound) bind S.string()
            .catch(PlatformDefaultSettings::errorNotificationSound)
        prop(IAppSettingsModel::successNotificationSound) bind S.string()
            .catch(PlatformDefaultSettings::successNotificationSound)
        prop(IAppSettingsModel::defaultDownloadFolder) bind S.string()
            .catch(PlatformDefaultSettings::defaultDownloadFolder)
        prop(IAppSettingsModel::apiEnabled) bind S.boolean().catch(PlatformDefaultSettings::apiEnabled)
        prop(IAppSettingsModel::apiPort) bind S.int().range(0, 65000).catch(PlatformDefaultSettings::apiPort)
        prop(IAppSettingsModel::apiAuthKey) bind S.string().catch(PlatformDefaultSettings::apiAuthKey)
        prop(IAppSettingsModel::apiAuthEnabled) bind S.boolean().catch(PlatformDefaultSettings::apiAuthEnabled)
        prop(IAppSettingsModel::webhookUrl) bind S.string().catch(PlatformDefaultSettings::webhookUrl)
        prop(IAppSettingsModel::trackDeletedFilesOnDisk) bind S.boolean()
            .catch(PlatformDefaultSettings::trackDeletedFilesOnDisk)
        prop(IAppSettingsModel::deletePartialFileOnDownloadCancellation) bind S.boolean()
            .catch(PlatformDefaultSettings::deletePartialFileOnDownloadCancellation)
        prop(IAppSettingsModel::sizeUnit) bind S.enum<SupportedSizeUnits>().catch(PlatformDefaultSettings::sizeUnit)
        prop(IAppSettingsModel::speedUnit) bind S.enum<SupportedSizeUnits>().catch(PlatformDefaultSettings::speedUnit)
        prop(IAppSettingsModel::ignoreSSLCertificates) bind S.boolean()
            .catch(PlatformDefaultSettings::ignoreSSLCertificates)
        prop(IAppSettingsModel::useCategoryByDefault) bind S.boolean()
            .catch(PlatformDefaultSettings::useCategoryByDefault)
        prop(IAppSettingsModel::organizeByType) bind S.boolean()
            .catch(PlatformDefaultSettings::organizeByType)
        prop(IAppSettingsModel::userAgent) bind S.string().catch(PlatformDefaultSettings::userAgent)
        prop(IAppSettingsModel::captureBlockedExtensions) bind S.string().catch(PlatformDefaultSettings::captureBlockedExtensions)
        prop(IAppSettingsModel::speedProfile) bind S.enum<SpeedProfile>().catch(PlatformDefaultSettings::speedProfile)
    }
}

