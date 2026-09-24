package grab.bit.desktop.actions

import grab.bit.desktop.AppComponent
import grab.bit.shared.util.SharedConstants
import grab.bit.desktop.di.Di
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.desktop.utils.AppInfo
import grab.bit.desktop.utils.DesktopEntryCreator
import grab.bit.desktop.utils.isAppInstalled
import grab.bit.desktop.window.Browser
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.action.buildMenu
import grab.bit.util.compose.action.simpleAction
import grab.bit.shared.util.getIcon
import grab.bit.shared.util.getName
import grab.bit.resources.Res
import grab.bit.shared.action.createCheckForUpdateAction
import grab.bit.shared.action.createDownloadFromClipboardAction
import grab.bit.shared.action.createNewDownloadAction
import grab.bit.shared.action.createNewQueueAction
import grab.bit.shared.action.createOpenAboutPage
import grab.bit.shared.action.createOpenBatchDownloadAction
import grab.bit.shared.action.createOpenOpenSourceThirdPartyLibrariesPage
import grab.bit.shared.action.createOpenQueuesAction
import grab.bit.shared.action.createOpenSettingsAction
import grab.bit.shared.action.createOpenTranslatorsPageAction
import grab.bit.shared.action.createPerHostSettingsPage
import grab.bit.shared.action.createRequestExitAction
import grab.bit.shared.action.createStartQueueGroupAction
import grab.bit.shared.action.createStopQueueGroupAction
import grab.bit.downloader.queue.activeQueuesFlow
import grab.bit.util.URLOpener
import grab.bit.util.compose.asStringSource
import grab.bit.util.desktop.PlatformAppActivator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.get

private val appComponent = Di.get<AppComponent>()
private val scope = Di.get<CoroutineScope>()
private val downloadSystem = appComponent.downloadSystem

private val activeQueuesFlow = downloadSystem
    .queueManager
    .activeQueuesFlow()
    .stateIn(
        scope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

// desktop
val stopAllAction = createDesktopStopAllAction(scope, downloadSystem, appComponent, activeQueuesFlow)
val newDownloadAction = createNewDownloadAction(appComponent)
val newDownloadFromClipboardAction = createDownloadFromClipboardAction(appComponent)
val createDesktopEntryAction = simpleAction(
    Res.string.create_desktop_entry.asStringSource(),
    MyIcons.applicationFile,
    checkEnable = MutableStateFlow(AppInfo.isAppInstalled())
) {
    DesktopEntryCreator.createLinuxDesktopEntry()
}
val showDownloadList = simpleAction(
    Res.string.show_downloads.asStringSource(),
    MyIcons.download,
) {
    PlatformAppActivator.active()
    appComponent.openHome()
}
val browserIntegrations = MenuItem.SubMenu(
    title = Res.string.download_browser_integration.asStringSource(),
    icon = MyIcons.download,
    items = buildMenu {
        for (browserExtension in SharedConstants.browserIntegrations) {
            item(
                title = browserExtension.type.getName().asStringSource(),
                icon = browserExtension.type.getIcon(),
                onClick = {
                    val browser = Browser.getBrowserByType(browserExtension.type)
                    val success = browser?.openLink(browserExtension.url) == true
                    if (!success) {
                        URLOpener.openUrl(browserExtension.url)
                    }
                }
            )
        }
    }
)


// commonUsage but with desktop implementations
val newQueueAction = createNewQueueAction(scope, appComponent)
val openQueuesAction = createOpenQueuesAction(appComponent)
val openTranslators = createOpenTranslatorsPageAction(appComponent)
val openAboutAction = createOpenAboutPage(appComponent)
val checkForUpdateAction = createCheckForUpdateAction(appComponent.updater)
val gotoSettingsAction = createOpenSettingsAction(appComponent)
val perHostSettings = createPerHostSettingsPage(appComponent)
val requestExitAction = createRequestExitAction(scope, appComponent)
val startQueueGroupAction = createStartQueueGroupAction(scope, appComponent.downloadSystem.queueManager)
val stopQueueGroupAction = createStopQueueGroupAction(scope, activeQueuesFlow)
val batchDownloadAction = createOpenBatchDownloadAction(appComponent)
val openOpenSourceThirdPartyLibraries = createOpenOpenSourceThirdPartyLibrariesPage(appComponent)
