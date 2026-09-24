package grab.bit.desktop.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import grab.bit.desktop.AppArguments
import grab.bit.desktop.AppComponent
import grab.bit.desktop.AppEffects
import grab.bit.desktop.actions.gotoSettingsAction
import grab.bit.desktop.actions.newDownloadFromClipboardAction
import grab.bit.desktop.actions.requestExitAction
import grab.bit.desktop.actions.showDownloadList
import grab.bit.desktop.pages.about.ShowAboutDialog
import grab.bit.desktop.pages.addDownload.ShowAddDownloadDialogs
import grab.bit.desktop.pages.batchdownload.BatchDownloadWindow
import grab.bit.desktop.pages.category.ShowCategoryDialogs
import grab.bit.desktop.pages.checksum.FileChecksumWindow
import grab.bit.desktop.pages.confirmexit.ConfirmExit
import grab.bit.desktop.pages.credits.translators.ShowTranslators
import grab.bit.desktop.pages.downloaderror.DownloadErrorDialog
import grab.bit.desktop.pages.editdownload.EditDownloadWindow
import grab.bit.desktop.pages.enterurl.EnterNewDownloadWindow
import grab.bit.desktop.pages.extenallibs.ShowOpenSourceLibraries
import grab.bit.desktop.pages.home.HomeWindow
import grab.bit.desktop.pages.newQueue.NewQueueDialog
import grab.bit.desktop.pages.perhostsettings.PerHostSettingsWindow
import grab.bit.desktop.pages.poweractionalert.PowerActionAlert
import grab.bit.desktop.pages.queue.QueuesWindow
import grab.bit.desktop.pages.settings.FontManager
import grab.bit.desktop.pages.settings.SettingWindow
import grab.bit.desktop.pages.singleDownloadPage.ShowDownloadDialogs
import grab.bit.desktop.pages.updater.ShowUpdaterDialog
import grab.bit.desktop.ui.configurable.comon.CommonConfigurableRenderersForDesktop
import grab.bit.desktop.ui.configurable.platform.PlatformConfigurableRenderersForDesktop
import grab.bit.desktop.ui.widget.ShowMessageDialogs
import grab.bit.desktop.ui.widget.Tray
import grab.bit.desktop.utils.AppInfo
import grab.bit.desktop.utils.GlobalAppExceptionHandler
import grab.bit.desktop.utils.ProvideGlobalExceptionHandler
import grab.bit.desktop.utils.isInDebugMode
import grab.bit.shared.ui.ProvideCommonSettings
import grab.bit.shared.ui.ProvideSizeUnits
import grab.bit.shared.ui.configurable.ConfigurableRendererRegistry
import grab.bit.shared.ui.theme.ABDownloaderTheme
import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.shared.ui.widget.NotificationManager
import grab.bit.shared.ui.widget.ProvideLanguageManager
import grab.bit.shared.ui.widget.ProvideNotificationManager
import grab.bit.shared.ui.widget.useNotification
import grab.bit.shared.util.mvi.HandleEffects
import grab.bit.shared.util.ui.ProvideDebugInfo
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.util.compose.action.buildMenu
import grab.bit.util.compose.localizationmanager.LanguageManager
import grab.bit.util.desktop.PlatformDockToggler
import grab.bit.util.desktop.mac.event.MacEventHandler
import grab.bit.util.platform.Platform
import grab.bit.util.platform.isMac
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

object Ui : KoinComponent {
    val scope: CoroutineScope by inject()
    val appComponent: AppComponent = get()
    val themeManager: ThemeManager = get()
    val fontManager: FontManager = get()
    val languageManager: LanguageManager = get()
    val notificationManager: NotificationManager = get()
    fun boot(
        appArguments: AppArguments,
    ) {
        themeManager.boot()
        fontManager.boot()
        languageManager.boot()
        if (!appArguments.startSilent) {
            appComponent.openHome()
        }
        if (Platform.isMac()) {
            MacEventHandler.configure(
                onClickIcon = appComponent::activateHomeIfNotOpen,
                onAboutClick = {
                    appComponent.showAboutPage.value = true
                },
                onSettingsClick = appComponent::openSettings,
                onQuit = {
                    scope.launch { appComponent.requestExitApp() }
                }
            )
        }
    }

    fun start(
        globalAppExceptionHandler: GlobalAppExceptionHandler,
    ) {
        application {
            ProvideLocalProviders(
                languageManager = languageManager,
                appComponent = appComponent,
                themeManager = themeManager,
                fontManager = fontManager,
                globalAppExceptionHandler = globalAppExceptionHandler,
                notificationManager = notificationManager,
            ) {
                HandleEffectsForApp(appComponent)
                SystemTray(appComponent)

                HomeWindow(appComponent)
                SettingWindow(appComponent)
                QueuesWindow(appComponent)
                BatchDownloadWindow(appComponent)
                EditDownloadWindow(appComponent)
                EnterNewDownloadWindow(appComponent)
                ShowAddDownloadDialogs(appComponent)
                ShowDownloadDialogs(appComponent)
                ShowCategoryDialogs(appComponent)
                FileChecksumWindow(appComponent)
                ShowUpdaterDialog(appComponent.updater)
                ShowAboutDialog(appComponent)
                NewQueueDialog(appComponent)
                ShowMessageDialogs(appComponent)
                ShowOpenSourceLibraries(appComponent)
                ShowTranslators(appComponent)
                ConfirmExit(appComponent)
                PowerActionAlert(appComponent)
                PerHostSettingsWindow(appComponent)
                DownloadErrorDialog(appComponent)
            }
        }
    }
}

@Composable
private fun ProvideLocalProviders(
    languageManager: LanguageManager,
    themeManager: ThemeManager,
    fontManager: FontManager,
    appComponent: AppComponent,
    notificationManager: NotificationManager,
    globalAppExceptionHandler: GlobalAppExceptionHandler,
    content: @Composable () -> Unit
) {
    val theme by themeManager.currentThemeColor.collectAsState()
    val fontFamily by fontManager.currentFontFamily.collectAsState()
    val configurableRendererRegistry = remember {
        ConfigurableRendererRegistry {
            listOf(
                PlatformConfigurableRenderersForDesktop,
                CommonConfigurableRenderersForDesktop,
            ).forEach {
                it.getAllRenderers().forEach { (key, renderer) ->
                    this.register(key, renderer)
                }
            }
        }
    }
    ProvideDebugInfo(AppInfo.isInDebugMode()) {
        ProvideLanguageManager(languageManager) {
            ProvideCommonSettings(
                appSettings = appComponent.appSettings,
                configurableRendererRegistry = configurableRendererRegistry,
                iconProvider = appComponent.iconFromUriResolver
            ) {
                ProvideNotificationManager(notificationManager) {
                    ABDownloaderTheme(
                        myColors = theme,
                        fontFamily = fontFamily,
                        uiScale = appComponent.uiScale.collectAsState().value
                    ) {
                        ProvideGlobalExceptionHandler(globalAppExceptionHandler) {
                            ProvideSizeUnits(appComponent.appRepository) {
                                content()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleEffectsForApp(appComponent: AppComponent) {
    val notificationManager = useNotification()
    val scope = rememberCoroutineScope()
    HandleEffects(appComponent) {
        when (it) {
            is AppEffects.SimpleNotificationNotification -> {
                scope.launch {
                    withTimeout(5.seconds) {
                        notificationManager.showNotification(it.notificationModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemTray(
    component: AppComponent,
) {
    val useSystemTray by component.useSystemTray.collectAsState()
    if (useSystemTray) {
        LaunchedEffect(Unit) { PlatformDockToggler.hide() }
        val menu = remember {
            buildMenu {
                +showDownloadList
                separator()
                +newDownloadFromClipboardAction
                separator()
                +gotoSettingsAction
                separator()
                +requestExitAction
            }
        }
        Tray(
            icon = MyIcons.appIcon,
            tooltip = AppInfo.displayName,
            primaryAction = { showDownloadList.onClick() },
            menu = menu,
        )
    } else {
        LaunchedEffect(Unit) { PlatformDockToggler.show() }
    }
}
