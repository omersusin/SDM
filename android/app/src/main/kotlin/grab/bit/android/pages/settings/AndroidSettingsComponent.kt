package grab.bit.android.pages.settings

import grab.bit.android.storage.AppSettingsStorage
import grab.bit.android.util.pagemanager.PermissionsPageManager
import grab.bit.shared.pagemanager.PerHostSettingsPageManager
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.settings.BaseSettingsComponent
import grab.bit.shared.settings.CommonSettings
import grab.bit.shared.storage.impl.DNSStorage
import grab.bit.shared.ui.configurable.ConfigurableGroup
import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.shared.util.proxy.ProxyManager
import com.arkivanov.decompose.ComponentContext
import grab.bit.util.compose.localizationmanager.LanguageManager
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class AndroidSettingsComponent(
    ctx: ComponentContext,
    perHostSettingsPageManager: PerHostSettingsPageManager,
    permissionsPageManager: PermissionsPageManager,
) : BaseSettingsComponent(
    ctx
), KoinComponent {
    private val appSettings by inject<AppSettingsStorage>()
    //    private val pageStorage by inject<PageStatesStorage>()
    private val appRepository by inject<BaseAppRepository>()
    private val proxyManager by inject<ProxyManager>()
    private val dnsStorage by inject<DNSStorage>()
    private val themeManager by inject<ThemeManager>()
    private val languageManager by inject<LanguageManager>()
    override val configurables: StateFlow<List<ConfigurableGroup>> = MutableStateFlow(
        listOf(
            ConfigurableGroup(
                mainConfigurable = CommonSettings.themeConfig(themeManager, scope),
                nestedVisible = themeManager.currentThemeInfo.mapStateFlow {
                    it.id == ThemeManager.systemThemeInfo.id
                },
                nestedConfigurable = listOfNotNull(
                    CommonSettings.defaultDarkThemeConfig(themeManager, scope),
                    CommonSettings.defaultLightThemeConfig(themeManager, scope),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.languageConfig(languageManager, scope),
//                            DesktopSettings.fontConfig(fontManager, scope),
                    CommonSettings.uiScaleConfig(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOfNotNull(
//                            DesktopSettings.useNativeMenuBarConfig(appSettings),
//                            DesktopSettings.mergeTopBarWithTitleBarConfig(appSettings),
//                    CommonSettings.showIconLabels(appSettings),
                    CommonSettings.useRelativeDateTime(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.autoStartConfig(appSettings),
//                            DesktopSettings.useSystemTray(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.sizeUnit(appRepository, scope),
                    CommonSettings.speedUnit(appRepository, scope),
                    CommonSettings.useAverageSpeedConfig(appRepository),
                )
            ),
            ConfigurableGroup(
                mainConfigurable = CommonSettings.playSoundNotification(appSettings),
                nestedVisible = appSettings.notificationSound,
                nestedConfigurable = listOf(
                    CommonSettings.generalNotificationSound(appSettings),
                    CommonSettings.errorNotificationSound(appSettings),
                    CommonSettings.successNotificationSound(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.autoShowDownloadProgressWindow(appSettings),
                    CommonSettings.showDownloadFinishWindow(appSettings),
                )
            ),
            // download engine

            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.defaultDownloadFolderConfig(appSettings),
                    CommonSettings.useCategoryByDefault(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.speedLimitConfig(appRepository),
                    CommonSettings.speedProfileConfig(appRepository, scope),
                    AndroidSettings.wifiOnlyDownloads(appSettings),
                    CommonSettings.threadCountConfig(appRepository),
                    CommonSettings.maxConcurrentDownloads(appRepository),
                    CommonSettings.maxDownloadRetryCount(appRepository),
                    CommonSettings.retryDelayConfig(appRepository),
                    CommonSettings.dynamicPartDownloadConfig(appRepository),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.perHostSettings(perHostSettingsPageManager),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.proxyConfig(proxyManager),
                    CommonSettings.dnsConfig(dnsStorage),
                    CommonSettings.userAgent(appSettings),
                    CommonSettings.ignoreSSLCertificates(appSettings),
                    CommonSettings.useServerLastModified(appRepository),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    CommonSettings.trackDeletedFilesOnDisk(appRepository),
                    CommonSettings.appendExtensionToIncompleteDownloads(appRepository),
                    CommonSettings.deletePartialFileOnDownloadCancellation(appSettings),
                    CommonSettings.autoUncompressArchives(appSettings),
                    CommonSettings.useSparseFileAllocation(appRepository),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    AndroidSettings.browserIconInLauncher(appSettings),
                    AndroidSettings.grabberUiMode(appSettings),
                    AndroidSettings.adBlockEnabled(appSettings),
                    AndroidSettings.videoQuality(appSettings),
                )
            ),
            ConfigurableGroup(
                nestedConfigurable = listOf(
                    AndroidSettings.permissionSettings(permissionsPageManager),
                    AndroidSettings.ignoreBatteryOptimizations(),
                )
            ),

            // browser integration
            // disabled for now
//            ConfigurableGroup(
//                nestedConfigurable = listOf(
//                    CommonSettings.browserIntegrationEnabled(appRepository),
//                    CommonSettings.browserIntegrationPort(appRepository)
//                )
//            )
        )
    )

}
