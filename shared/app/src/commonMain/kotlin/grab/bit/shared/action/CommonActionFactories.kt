package grab.bit.shared.action

import grab.bit.resources.Res
import grab.bit.shared.pagemanager.*
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.pages.updater.UpdateComponent
import grab.bit.shared.util.ClipboardUtil
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.SharedConstants
import grab.bit.shared.util.category.Category
import grab.bit.shared.util.extractors.linkextractor.DefaultDownloadCredentialsExtractor
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.downloader.queue.DownloadQueue
import grab.bit.downloader.queue.QueueManager
import grab.bit.downloader.queue.inactiveQueuesFlow
import grab.bit.util.URLOpener
import grab.bit.util.compose.action.AnAction
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.action.buildMenu
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource
import grab.bit.util.flow.combineStateFlows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


fun createNewDownloadAction(
    enterNewURLDialogManager: EnterNewURLDialogManager,
): AnAction {
    return simpleAction(
        Res.string.new_download.asStringSource(),
        MyIcons.add,
    ) {
        enterNewURLDialogManager.openEnterNewURLWindow()
    }
}

fun createDownloadFromClipboardAction(
    addDownloadDialogManager: AddDownloadDialogManager,
): AnAction {
    return simpleAction(
        Res.string.import_from_clipboard.asStringSource(),
        MyIcons.paste,
    ) {
        val contentsInClipboard = ClipboardUtil.read()
        if (contentsInClipboard.isNullOrEmpty()) {
            return@simpleAction
        }

        val items = DefaultDownloadCredentialsExtractor
            .extract(contentsInClipboard)

        if (items.isNotEmpty()) {
            addDownloadDialogManager.openAddDownloadDialog(
                items.map {
                    AddDownloadCredentialsInUiProps(it)
                }
            )
        }
    }
}

fun createOpenBatchDownloadAction(
    batchDownloadPageManager: BatchDownloadPageManager
): AnAction {
    return simpleAction(
        title = Res.string.batch_download.asStringSource(),
        icon = MyIcons.download
    ) {
        batchDownloadPageManager.openBatchDownloadPage()
    }
}


fun createStopQueueGroupAction(
    scope: CoroutineScope,
    activeQueuesFlow: StateFlow<List<DownloadQueue>>
): MenuItem.SubMenu {
    return MenuItem.SubMenu(
        icon = MyIcons.queueStop,
        title = Res.string.stop_queue.asStringSource(),
        items = emptyList()
    ).apply {
        activeQueuesFlow
            .onEach {
                setItems(it.map {
                    createStopQueueAction(scope, it)
                })
            }.launchIn(scope)
    }
}

fun createStartQueueGroupAction(
    scope: CoroutineScope,
    queueManager: QueueManager,
): MenuItem.SubMenu {
    return MenuItem.SubMenu(
        icon = MyIcons.queueStart,
        title = Res.string.start_queue.asStringSource(),
        items = emptyList()
    ).apply {
        queueManager
            .inactiveQueuesFlow()
            .onEach {
                setItems(it.map {
                    createStartQueueAction(scope, it)
                })
            }.launchIn(scope)
    }
}

// ui exit
fun createRequestExitAction(
    scope: CoroutineScope,
    exitAppRequestManager: ExitApplicationRequestManager
): AnAction {
    return simpleAction(
        Res.string.exit.asStringSource(),
        MyIcons.exit,
    ) {
        scope.launch { exitAppRequestManager.requestExitApp() }
    }
}


fun createPerHostSettingsPage(
    perHostSettingsPageManager: PerHostSettingsPageManager
): AnAction {
    return simpleAction(
        Res.string.settings_per_host_settings.asStringSource(),
        MyIcons.earth,
    ) {
        perHostSettingsPageManager.openPerHostSettings(null)
    }
}


fun createOpenSettingsAction(
    settingsPageManager: SettingsPageManager
): AnAction {
    return simpleAction(
        Res.string.settings.asStringSource(),
        MyIcons.settings,
    ) {
        settingsPageManager.openSettings()
    }
}

fun createCheckForUpdateAction(
    updaterComponent: UpdateComponent
): AnAction {
    return simpleAction(
        title = Res.string.update_check_for_update.asStringSource(),
        icon = MyIcons.refresh,
        checkEnable = MutableStateFlow(
            updaterComponent.isUpdateSupported()
        )
    ) {
        updaterComponent.requestCheckForUpdate()
    }
}


fun createOpenAboutPage(aboutPageManager: AboutPageManager): AnAction {
    return simpleAction(
        title = Res.string.about.asStringSource(),
        icon = MyIcons.info,
    ) {
        aboutPageManager.openAboutPage()
    }
}

fun createOpenOpenSourceThirdPartyLibrariesPage(
    openSourceLibrariesPageManager: OpenSourceLibrariesPageManager,
): AnAction {
    return simpleAction(
        title = Res.string.view_the_open_source_licenses.asStringSource(),
        icon = MyIcons.openSource,
    ) {
        openSourceLibrariesPageManager.openOpenSourceLibrariesPage()
    }
}

fun createOpenTranslatorsPageAction(
    opeTranslatorsPageManager: TranslatorsPageManager,
): AnAction {
    return simpleAction(
        title = Res.string.meet_the_translators.asStringSource(),
        icon = MyIcons.language,
    ) {
        opeTranslatorsPageManager.openTranslatorsPage()
    }
}

val donate = simpleAction(
    title = Res.string.donate.asStringSource(),
    icon = MyIcons.hearth,
) {
    URLOpener.openUrl(SharedConstants.donateLink)
}

val supportActionGroup = MenuItem.SubMenu(
    title = Res.string.support_and_community.asStringSource(),
    icon = MyIcons.group,
    items = buildMenu {
        item(Res.string.website.asStringSource(), MyIcons.appIcon) {
            URLOpener.openUrl(SharedConstants.projectWebsite)
        }
        item(Res.string.source_code.asStringSource(), MyIcons.openSource) {
            URLOpener.openUrl(SharedConstants.projectSourceCode)
        }
        subMenu(Res.string.telegram.asStringSource(), MyIcons.telegram) {
            item(Res.string.channel.asStringSource(), MyIcons.speaker) {
                URLOpener.openUrl(SharedConstants.telegramChannelUrl)
            }
            item(Res.string.group.asStringSource(), MyIcons.group) {
                URLOpener.openUrl(SharedConstants.telegramGroupUrl)
            }
        }
    }
)

fun createOpenQueuesAction(
    queuePageManager: QueuePageManager
): AnAction {
    return simpleAction(
        title = Res.string.queues.asStringSource(),
        icon = MyIcons.queue
    ) {
        queuePageManager.openQueues()
    }
}


fun createMoveToQueueAction(
    scope: CoroutineScope,
    downloadSystem: DownloadSystem,
    queue: DownloadQueue,
    itemId: List<Long>,
): AnAction {
    return simpleAction(queue.getQueueModel().name.asStringSource()) {
        scope.launch {
            downloadSystem
                .queueManager
                .addToQueue(
                    queueId = queue.id,
                    downloadIds = itemId,
                )
        }
    }
}

fun createMoveToCategoryAction(
    scope: CoroutineScope,
    downloadSystem: DownloadSystem,
    category: Category,
    itemIds: List<Long>,
): AnAction {
    return simpleAction(category.name.asStringSource()) {
        scope.launch {
            downloadSystem
                .categoryManager
                .addItemsToCategory(
                    categoryId = category.id,
                    itemIds = itemIds,
                )
        }
    }
}

fun createStopQueueAction(
    scope: CoroutineScope,
    queue: DownloadQueue,
): AnAction {
    return simpleAction(queue.getQueueModel().name.asStringSource()) {
        scope.launch {
            queue.stop()
        }
    }
}

fun createStartQueueAction(
    scope: CoroutineScope,
    queue: DownloadQueue,
): AnAction {
    return simpleAction(queue.getQueueModel().name.asStringSource()) {
        scope.launch {
            queue.start()
        }
    }
}

fun createNewQueueAction(
    scope: CoroutineScope,
    queuePageManager: NewQueuePageManager,
): AnAction {
    return simpleAction(Res.string.add_new_queue.asStringSource()) {
        scope.launch {
            queuePageManager.openNewQueueDialog()
        }
    }
}

fun createStopAllAction(
    scope: CoroutineScope,
    downloadSystem: DownloadSystem,
    extraJobs: () -> Unit,
    activeQueuesFlow: StateFlow<List<DownloadQueue>>
): AnAction {
    return simpleAction(
        Res.string.stop_all.asStringSource(),
        MyIcons.stop,
        checkEnable = combineStateFlows(
            downloadSystem.downloadMonitor.activeDownloadCount,
            activeQueuesFlow
        ) { downloadCount, activeQueues ->
            downloadCount > 0 || activeQueues.isNotEmpty()
        }
    ) {
        scope.launch {
            downloadSystem.stopAnything()
            extraJobs()
        }
    }
}
