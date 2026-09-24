package grab.bit.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import grab.bit.android.ui.configurable.comon.CommonConfigurableRenderersForAndroid
import grab.bit.android.ui.configurable.comon.ConfigurableRenderersForAndroid
import grab.bit.android.util.AppInfo
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.ui.ProvideCommonSettings
import grab.bit.shared.ui.ProvideSizeUnits
import grab.bit.shared.ui.configurable.ConfigurableRendererRegistry
import grab.bit.shared.ui.theme.ABDownloaderTheme
import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.shared.ui.widget.NotificationManager
import grab.bit.shared.ui.widget.ProvideLanguageManager
import grab.bit.shared.ui.widget.ProvideNotificationManager
import grab.bit.shared.util.PopUpContainer
import grab.bit.shared.util.ResponsiveBox
import grab.bit.shared.util.ui.ProvideDebugInfo
import grab.bit.util.compose.IIconResolver
import grab.bit.util.compose.localizationmanager.LanguageManager
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ABDownloadManagerApplicationContent(
    languageManager: LanguageManager,
    themeManager: ThemeManager,
    appSettingsStorage: BaseAppSettingsStorage,
    iconResolver: IIconResolver,
    appRepository: BaseAppRepository,
    notificationManager: NotificationManager,
    content: @Composable () -> Unit,
) {
    val configurableRendererRegistry = remember {
        ConfigurableRendererRegistry {
            listOf(
                CommonConfigurableRenderersForAndroid,
                ConfigurableRenderersForAndroid
            ).forEach {
                it.getAllRenderers().forEach { (key, renderer) ->
                    this.register(key, renderer)
                }
            }
        }
    }
    ProvideDebugInfo(AppInfo.isInDebugMode) {
        ProvideLanguageManager(languageManager) {
            ProvideCommonSettings(
                appSettings = appSettingsStorage,
                iconProvider = iconResolver,
                configurableRendererRegistry = configurableRendererRegistry,
            ) {
                ProvideNotificationManager(notificationManager) {
                    val myColors by themeManager.currentThemeColor.collectAsState()
                    val uiScale by appSettingsStorage.uiScale.collectAsState()
                    ABDownloaderTheme(
                        myColors = myColors,
                        fontFamily = null,
                        uiScale = uiScale,
                    ) {
                        ResponsiveBox {
                            ProvideSizeUnits(
                                appRepository
                            ) {
                                PopUpContainer {
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
