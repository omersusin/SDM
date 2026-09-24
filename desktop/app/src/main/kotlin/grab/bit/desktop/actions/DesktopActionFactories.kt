package grab.bit.desktop.actions

import grab.bit.desktop.DesktopDownloadDialogManager
import grab.bit.shared.action.createStopAllAction
import grab.bit.shared.util.DownloadSystem
import grab.bit.downloader.queue.DownloadQueue
import grab.bit.util.compose.action.AnAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow


fun createDesktopStopAllAction(
    scope: CoroutineScope,
    downloadSystem: DownloadSystem,
    desktopDownloadDialogManager: DesktopDownloadDialogManager,
    activeQueuesFlow: StateFlow<List<DownloadQueue>>
): AnAction {
    return createStopAllAction(
        scope = scope,
        downloadSystem = downloadSystem,
        activeQueuesFlow = activeQueuesFlow,
        extraJobs = {
            val activeDownloadIds = downloadSystem.downloadMonitor.activeDownloadListFlow.value.map { it.id }
            desktopDownloadDialogManager.closeDownloadDialog(activeDownloadIds)
        }
    )
}
