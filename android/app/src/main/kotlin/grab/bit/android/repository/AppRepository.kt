package grab.bit.android.repository

import grab.bit.android.pages.browser.BrowserActivity
import grab.bit.android.storage.AppSettingsStorage
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.autoremove.RemovedDownloadsFromDiskTracker
import grab.bit.shared.util.category.CategoryManager
import grab.bit.shared.util.proxy.ProxyManager
import grab.bit.downloader.DownloadSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AppRepository(
    scope: CoroutineScope,
    appSettings: AppSettingsStorage,
    proxyManager: ProxyManager,
    downloadSystem: DownloadSystem,
    downloadSettings: DownloadSettings,
    removedDownloadsFromDiskTracker: RemovedDownloadsFromDiskTracker,
    categoryManager: CategoryManager,
) : BaseAppRepository(
    scope = scope,
    appSettings = appSettings,
    proxyManager = proxyManager,
    downloadSystem = downloadSystem,
    downloadSettings = downloadSettings,
    removedDownloadsFromDiskTracker = removedDownloadsFromDiskTracker,
    categoryManager = categoryManager,
) {
    init {
        appSettings.browserIconInLauncher
            .debounce(500)
            .distinctUntilChanged()
            .onEach { enabled ->
                BrowserActivity.Companion.Launcher.setEnabled(enabled)
            }.launchIn(scope)
    }
}
