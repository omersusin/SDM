package grab.bit.android.pages.home

import grab.bit.resources.Res
import grab.bit.shared.pagemanager.DownloadDialogManager
import grab.bit.shared.pagemanager.EditDownloadDialogManager
import grab.bit.shared.pagemanager.FileChecksumDialogManager
import grab.bit.shared.pages.home.AbstractDownloadActions
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.downloader.downloaditem.DownloadJobStatus
import grab.bit.downloader.monitor.CompletedDownloadItemState
import grab.bit.downloader.monitor.IDownloadItemState
import grab.bit.downloader.monitor.statusOrFinished
import grab.bit.downloader.queue.QueueManager
import grab.bit.util.compose.action.buildMenu
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AndroidDownloadActions(
    scope: CoroutineScope,
    downloadSystem: DownloadSystem,
    downloadDialogManager: DownloadDialogManager,
    editDownloadDialogManager: EditDownloadDialogManager,
    fileChecksumDialogManager: FileChecksumDialogManager,
    selections: StateFlow<List<IDownloadItemState>>,
    mainItem: StateFlow<Long?>,
    queueManager: QueueManager,
    categoryManager: CategoryManager,
    openFile: (Long) -> Unit,
    requestDelete: (List<Long>) -> Unit,
    onRequestShareFiles: (ids: List<CompletedDownloadItemState>) -> Unit,
) : AbstractDownloadActions(
    scope = scope,
    downloadSystem = downloadSystem,
    downloadDialogManager = downloadDialogManager,
    editDownloadDialogManager = editDownloadDialogManager,
    fileChecksumDialogManager = fileChecksumDialogManager,
    selections = selections,
    mainItem = mainItem,
    queueManager = queueManager,
    categoryManager = categoryManager,
    openFile = openFile,
    requestDelete = requestDelete,
) {
    val shareAction = simpleAction(
        title = Res.string.share.asStringSource(),
        icon = MyIcons.share,
        checkEnable = selections.mapStateFlow { list ->
            list.any { it.statusOrFinished() is DownloadJobStatus.Finished }
        },
        onActionPerformed = {
            scope.launch {
                onRequestShareFiles(selections.value.filterIsInstance<CompletedDownloadItemState>())
            }
        }
    )
    private val mainOptions = buildMenu {
        +resumeAction
        +pauseAction
        +deleteAction
        +openDownloadDialogAction
    }
    private val extraMenu = buildMenu {
        +openFileAction
        +shareAction
        separator()
        +reDownloadAction
        separator()
        +moveToQueueItems
        +moveToCategoryAction
        separator()
        subMenu(Res.string.copy.asStringSource(), MyIcons.copy) {
            +(copyDownloadLinkAction)
            +(copyDownloadCredentialsAsJsonAction)
            +(copyDownloadCredentialsAsCurlAction)
        }
        +editDownloadAction
        +fileChecksumAction
    }
    val androidMenu = buildMenu {
        mainOptions.forEach {
            +it
        }
        subMenu(
            title = Res.string.more_options.asStringSource(),
            icon = MyIcons.menu,
        ) {
            extraMenu.forEach { +it }
        }
    }
}
