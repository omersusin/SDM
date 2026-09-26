package grab.bit.android.pages.browser

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Stable
import grab.bit.android.pages.add.multiple.AddMultiDownloadActivity
import grab.bit.android.pages.add.single.AddSingleDownloadActivity
import grab.bit.android.pages.browser.bookmark.EditBookmarkState
import grab.bit.android.storage.BrowserBookmark
import grab.bit.android.storage.BrowserBookmarksStorage
import grab.bit.android.storage.AppSettingsStorage
import grab.bit.android.ui.widget.WebContent
import grab.bit.android.ui.widget.WebViewState
import grab.bit.resources.Res
import grab.bit.shared.pages.adddownload.AddDownloadConfig
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.util.BaseComponent
import grab.bit.shared.util.ClipboardUtil
import grab.bit.shared.util.mvi.ContainsEffects
import grab.bit.shared.util.mvi.supportEffects
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import grab.bit.shared.util.ui.icon.MyIcons
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.queue.CapturedLink
import grab.bit.downloader.queue.LinkPool
import grab.bit.downloader.torrent.MagnetParser
import grab.bit.downloader.torrent.TorrentSession
import grab.bit.downloader.torrent.createTorrentSession
import grab.bit.downloader.video.VideoFormat
import grab.bit.downloader.video.VideoFormatPicker
import grab.bit.downloader.video.YtDlpDownloadRequest
import grab.bit.downloader.video.YtDlpInfoParser
import grab.bit.downloader.video.YtDlpRunner
import grab.bit.downloader.video.YtDlpSubtitle
import grab.bit.util.HttpUrlUtils
import grab.bit.util.GrabberUiMode
import grab.bit.util.MediaCandidate
import grab.bit.util.compose.action.AnAction
import grab.bit.util.compose.action.MenuItem
import grab.bit.util.compose.action.buildMenu
import grab.bit.util.compose.action.simpleAction
import grab.bit.util.compose.asStringSource
import grab.bit.util.ifThen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.text.orEmpty
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class BrowserComponent(
    componentContext: ComponentContext,
    private val context: Context,
    private val json: Json,
    private val browserBookmarksStorage: BrowserBookmarksStorage,
) : BaseComponent(
    componentContext,
), ContainsEffects<BrowserComponent.Effects> by supportEffects(), KoinComponent {
    private val appSettings by inject<AppSettingsStorage>()
    private val perHostSettings by inject<PerHostSettingsManager>()
    val downloadInterceptor = DownloadInterceptor(
        scope, perHostSettings, {
            val intent = when (it.size) {
                0 -> null
                1 -> AddSingleDownloadActivity.createIntent(
                    context,
                    AddDownloadConfig.SingleAddConfig(it.first()),
                    json,
                )

                else -> AddMultiDownloadActivity.createIntent(
                    context,
                    AddDownloadConfig.MultipleAddConfig(
                        it
                    ),
                    json
                )
            }
            intent?.let { intent ->
                sendEffect(Effects.StartActivity(intent))
            }
        }
    )
    private val currentSearchEngine = MutableStateFlow(
        SearchEngines.DuckDuckGo
    )
    val tabs = MutableStateFlow(
        ABDMTabs.createDefault()
    )
    val bookmarks = browserBookmarksStorage.bookmarksFlow
    init {
        scope.launch(Dispatchers.IO) {
            AdBlockLists.ensureLoaded(context, downloadInterceptor)
        }
        scope.launch {
            appSettings.adBlockEnabled.collect {
                downloadInterceptor.adBlockEnabled = it
                if (it) {
                    AdBlockLists.ensureLoaded(context, downloadInterceptor)
                }
            }
        }
    }
    val grabberUiMode: StateFlow<GrabberUiMode> = appSettings.grabberUiMode
    val activeMediaCount: StateFlow<Int> = combine(
        tabs, downloadInterceptor.mediaCounts
    ) { tabsState, counts ->
        val page = tabsState.activeTab?.tabState?.lastLoadedUrl
        if (page != null) counts[page] ?: 0 else 0
    }.stateIn(scope, SharingStarted.Eagerly, 0)

    fun mediaForActivePage(): List<MediaCandidate> {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl ?: return emptyList()
        return downloadInterceptor.mediaForPage(page)
    }

    fun downloadMedia(url: String) {
        val tab = tabs.value.activeTab ?: return
        downloadInterceptor.onDownloadStart(
            url = url,
            userAgent = null,
            page = tab.tabState.lastLoadedUrl,
            tab = tab,
        )
    }

    private val _showMediaList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showMediaList = _showMediaList.asStateFlow()
    fun setShowMediaList(show: Boolean) {
        _showMediaList.value = show
    }

    private val _autoPopupShownFor: MutableStateFlow<String?> = MutableStateFlow(null)
    fun autoPopupIfNeeded() {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl ?: return
        if (_autoPopupShownFor.value == page) return
        _autoPopupShownFor.value = page
        setShowMediaList(true)
    }

    sealed interface VideoFormatsState {
        data object Closed : VideoFormatsState
        data object Loading : VideoFormatsState
        data class Ready(
            val formats: List<VideoFormat>,
            val subtitles: Map<String, List<YtDlpSubtitle>>,
        ) : VideoFormatsState
    }

    private val _videoFormats: MutableStateFlow<VideoFormatsState> =
        MutableStateFlow(VideoFormatsState.Closed)
    val videoFormats = _videoFormats.asStateFlow()

    fun openVideoFormats() {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl ?: return
        _videoFormats.value = VideoFormatsState.Loading
        scope.launch(Dispatchers.IO) {
            val info = YtDlpRunner.dumpInfo(page)
                ?.let { runCatching { YtDlpInfoParser.parse(it) }.getOrNull() }
            val formats = info?.let { YtDlpInfoParser.toVideoFormats(it) }.orEmpty()
            val videos = VideoFormatPicker.videoFormats(formats)
            val best = VideoFormatPicker.pickForQuality(videos, appSettings.videoQuality.value)
            _selectedSubs.value = emptySet()
            _videoFormats.value = VideoFormatsState.Ready(
                listOfNotNull(best) + videos.filter { it != best },
                info?.subtitles.orEmpty(),
            )
        }
    }

    fun closeVideoFormats() {
        _videoFormats.value = VideoFormatsState.Closed
    }

    private val _selectedSubs: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val selectedSubs = _selectedSubs.asStateFlow()

    fun toggleSubtitle(lang: String) {
        _selectedSubs.update {
            if (lang in it) it - lang else it + lang
        }
    }

    fun downloadVideoFormat(format: VideoFormat) {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl ?: return
        val saveDir = appSettings.defaultDownloadFolder.value
        val langs = _selectedSubs.value.toList()
        scope.launch(Dispatchers.IO) {
            runCatching {
                YtDlpRunner.download(
                    YtDlpDownloadRequest(
                        url = page,
                        formatId = format.id,
                        subtitleLangs = langs,
                        outputTemplate = "%(title)s.%(ext)s",
                    ),
                    saveDir,
                )
            }
        }
        closeVideoFormats()
    }

    val linkPool = LinkPool()

    private var torrentSession: TorrentSession? = null

    data class TorrentItem(
        val infoHash: String,
        val name: String?,
        val progress: Float?,
    )

    private val _torrents = MutableStateFlow(emptyList<TorrentItem>())
    val torrents = _torrents.asStateFlow()

    private var torrentPolling = false

    fun clipboardHasMagnet(): Boolean {
        return ClipboardUtil.read()?.startsWith("magnet:?", ignoreCase = true) == true
    }

    fun addTorrentFromClipboard(): Boolean {
        val magnet = ClipboardUtil.read()?.let { MagnetParser.parse(it) } ?: return false
        scope.launch(Dispatchers.IO) {
            runCatching {
                val session = torrentSession ?: createTorrentSession().also {
                    it.start()
                    torrentSession = it
                }
                if (session.addMagnet(magnet)) {
                    _torrents.update {
                        (it + TorrentItem(magnet.infoHash, magnet.name, null))
                            .distinctBy { item -> item.infoHash }
                    }
                    startTorrentPolling()
                }
            }
        }
        return true
    }

    private fun startTorrentPolling() {
        if (torrentPolling) return
        torrentPolling = true
        scope.launch(Dispatchers.IO) {
            while (true) {
                delay(2000)
                val session = torrentSession ?: break
                _torrents.update { items ->
                    items.map { item ->
                        item.copy(
                            progress = session.progress(item.infoHash)?.progress
                        )
                    }
                }
            }
        }
    }

    fun pauseAllTorrents() {
        scope.launch(Dispatchers.IO) {
            runCatching { torrentSession?.pauseAll() }
        }
    }

    fun resumeAllTorrents() {
        scope.launch(Dispatchers.IO) {
            runCatching { torrentSession?.resumeAll() }
        }
    }

    fun pauseTorrent(infoHash: String) {
        scope.launch(Dispatchers.IO) {
            runCatching { torrentSession?.pause(infoHash) }
        }
    }

    fun resumeTorrent(infoHash: String) {
        scope.launch(Dispatchers.IO) {
            runCatching { torrentSession?.resume(infoHash) }
        }
    }

    fun capturePageMedia() {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl
        linkPool.addAll(
            mediaForActivePage().mapNotNull {
                when (it) {
                    is MediaCandidate.Direct -> CapturedLink(it.url, page, it.fileName)
                    is MediaCandidate.Stream -> CapturedLink(it.url, page, null)
                    MediaCandidate.NotMedia -> null
                }
            }
        )
    }

    private val _showPool: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPool = _showPool.asStateFlow()
    fun setShowPool(show: Boolean) {
        _showPool.value = show
    }

    private val _showTorrents: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showTorrents = _showTorrents.asStateFlow()
    fun setShowTorrents(show: Boolean) {
        _showTorrents.value = show
    }

    fun poolLinks(): List<CapturedLink> {
        return linkPool.selectedLinks()
    }

    fun downloadPoolUrls(urls: Collection<String>) {
        val page = tabs.value.activeTab?.tabState?.lastLoadedUrl
        val props = urls.mapNotNull { url ->
            linkPool.selectedLinks().find { it.url == url }?.let {
                AddDownloadCredentialsInUiProps(
                    HttpDownloadCredentials(
                        link = it.url,
                        headers = emptyMap(),
                        downloadPage = it.page ?: page,
                    ),
                    AddDownloadCredentialsInUiProps.Configs()
                )
            }
        }
        if (props.isEmpty()) return
        linkPool.removeUrls(urls)
        val intent = AddMultiDownloadActivity.createIntent(
            context,
            AddDownloadConfig.MultipleAddConfig(props),
            json,
        )
        sendEffect(Effects.StartActivity(intent))
    }
    private val _mainMenu: MutableStateFlow<MenuItem.SubMenu?> = MutableStateFlow(null)
    val mainMenu = _mainMenu.asStateFlow()
    fun openMainMenu() {
        val tab = tabs.value.activeTab
        val url = tab?.tabState?.lastLoadedUrl
        val title = tab?.tabState?.pageTitle
        _mainMenu.value = MenuItem.SubMenu(
            title = title?.asStringSource() ?: Res.string.menu.asStringSource(),
            items = buildMenu {
                +createNewTabAction()
                separator()
                +createShowBookmarksAction()
                if (url != null) {
                    if (isBookmarked(url)) {
                        +createRemoveFromBookmarkAction(url)
                    } else {
                        +createAddToBookmarkAction(url, title)
                    }
                }
                tab?.let {
                    separator()
                    +createCloseTabAction(it)
                }
                if (url != null) {
                    separator()
                    +simpleAction(
                        Res.string.browser_video_formats.asStringSource(),
                        MyIcons.videoFile,
                    ) {
                        openVideoFormats()
                    }
                }
                if (clipboardHasMagnet()) {
                    separator()
                    +simpleAction(
                        Res.string.browser_add_torrent.asStringSource(),
                        MyIcons.download,
                    ) {
                        addTorrentFromClipboard()
                    }
                }
                if (linkPool.size > 0) {
                    separator()
                    +simpleAction(
                        Res.string.browser_pool.asStringSource(),
                        MyIcons.download,
                    ) {
                        setShowPool(true)
                    }
                }
                if (torrents.value.isNotEmpty()) {
                    separator()
                    +simpleAction(
                        Res.string.browser_torrents.asStringSource(),
                        MyIcons.download,
                    ) {
                        setShowTorrents(true)
                    }
                }
            }
        )
    }

    fun closeMainMenu() {
        _mainMenu.value = null
    }

    fun newTab(
        url: String? = ABDMBrowserTab.blankPage,
        switch: Boolean = true,
        id: String = UUID.randomUUID().toString(),
        openedBy: ABDMBrowserTabId? = null,
    ): ABDMBrowserTab {
        val browserTab = ABDMBrowserTab(
            tabId = id,
            tabState = WebViewState(WebContent.fromNullableUrl(url)),
        )
        tabs.update { currentTabState ->
            val newTabPosition = openedBy?.let {
                // index of openedBy + 1 or null if not found
                currentTabState.tabs
                    .indexOfFirst { it.tabId == openedBy }
                    .takeIf { it >= 0 }
                    ?.plus(1)
            } ?: currentTabState.tabs.size
            val newItems = buildList {
                addAll(currentTabState.tabs)
                add(newTabPosition, browserTab)
            }
            val newIndex = if (switch) {
                newTabPosition
            } else {
                currentTabState.activeTabIndex
            }
            currentTabState.copy(
                tabs = newItems,
                activeTabIndex = newIndex
            )
        }
        return browserTab
    }

    fun closeTab(tabId: ABDMBrowserTabId) {
        tabs.update {
            val newItems = it.tabs.filterNot { it.tabId == tabId }
            it.copy(
                tabs = newItems,
                activeTabIndex = runCatching {
                    it.activeTabIndex.coerceIn(newItems.indices)
                }.getOrElse { -1 },
            )
        }
    }

    fun addToBookmarks(
        bookmark: BrowserBookmark,
        replaceWith: BrowserBookmark?,
    ) {
        browserBookmarksStorage.bookmarksFlow.update { currentBookmarks ->
            if (replaceWith != null) {
                currentBookmarks.map { item ->
                    item.ifThen(item == replaceWith) {
                        bookmark
                    }
                }
            } else {
                currentBookmarks.plus(bookmark)
            }
        }
    }

    private val _showBookmarkList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showBookmarkList = _showBookmarkList.asStateFlow()
    fun setShowBookmarkList(show: Boolean) {
        _showBookmarkList.value = show
    }

    private val _editBookmarkState = MutableStateFlow<EditBookmarkState?>(null)
    val editBookmarkState = _editBookmarkState.asStateFlow()
    fun promptAddBookmark(
        bookmark: BrowserBookmark,
    ) {
        _editBookmarkState.value = EditBookmarkState(
            initialValue = bookmark,
            editMode = false,
        )
    }

    fun promptEditBookmark(
        bookmark: BrowserBookmark
    ) {
        _editBookmarkState.value = EditBookmarkState(
            initialValue = bookmark,
            editMode = true,
        )
    }

    fun dismissEditBookmark() {
        _editBookmarkState.value = null
    }

    fun removeBookmark(url: String) {
        browserBookmarksStorage.bookmarksFlow.update {
            it.filterNot { bookmark -> bookmark.url == url }
        }
    }

    fun isBookmarked(url: String): Boolean {
        return browserBookmarksStorage.bookmarksFlow.value.find {
            it.url == url
        } != null
    }

    fun switchTab(tabId: ABDMBrowserTabId) {
        tabs.update {
            val tabIndex = it.tabs.indexOfFirst { it.tabId == tabId }
            val newIndex = if (tabIndex < 0) {
                it.activeTabIndex
            } else {
                tabIndex
            }
            it.copy(
                activeTabIndex = newIndex,
            )
        }
    }

    private val websiteAndTLD by lazy {
        """^[\w.-]+\.[a-zA-Z]{2,}(:\d{1,5})?$""".toRegex()
    }

    fun createNewUrlFor(urlOrSearch: String): String {
        val value = urlOrSearch.trim()
        if (value.contains(' ')) {
            return createSearchEngineUrl(value)
        }
        if (HttpUrlUtils.isValidUrl(value)) {
            return value
        }
        if (websiteAndTLD.matches(value)) {
            val withHttpScheme = "https://$value"
            if (HttpUrlUtils.isValidUrl(withHttpScheme)) {
                return withHttpScheme
            }
        }
        return createSearchEngineUrl(value)
    }

    private fun createSearchEngineUrl(searchText: String): String {
        return currentSearchEngine.value.createSearchUrl(searchText)
    }

    val contextMenu: MutableStateFlow<MenuItem.SubMenu?> = MutableStateFlow(null)

    fun closeContextMenu() {
        contextMenu.value = null
    }

    fun onLinkSelected(
        link: String,
        tab: ABDMBrowserTab,
    ) {
        contextMenu.value = MenuItem.SubMenu(
            title = link.asStringSource(),
            items = buildMenu {
                +simpleAction(
                    Res.string.browser_open_in_new_tab.asStringSource(),
                    MyIcons.file,
                ) {
                    newTab(
                        url = link,
                        switch = true,
                        openedBy = tab.tabId,
                    )
                }
                +simpleAction(
                    Res.string.browser_open_in_new_background_tab.asStringSource(),
                    MyIcons.file,
                ) {
                    newTab(
                        url = link,
                        switch = false,
                        openedBy = tab.tabId,
                    )
                }
                +simpleAction(
                    Res.string.share.asStringSource(),
                    MyIcons.share,
                ) {
                    sendEffect(Effects.ShareText(link))
                }
                +simpleAction(
                    Res.string.copy.asStringSource(),
                    MyIcons.copy,
                ) {
                    ClipboardUtil.copy(link)
                }
                +simpleAction(
                    Res.string.download.asStringSource(),
                    MyIcons.download,
                ) {
                    downloadInterceptor.onDownloadStart(
                        url = link,
                        userAgent = null,
                        page = null,
                        tab = tab,
                    )
                }
                if (isBookmarked(link)) {
                    +createRemoveFromBookmarkAction(link)
                } else {
                    +createAddToBookmarkAction(link, null)
                }
            }
        )
    }

    fun createNewTabAction(): AnAction {
        return simpleAction(
            title = Res.string.browser_new_tab.asStringSource(),
            icon = MyIcons.file,
        ) {
            newTab(
                url = null,
                switch = true,
            )
        }
    }

    fun createAddToBookmarkAction(
        url: String,
        title: String?,
    ): AnAction {
        return simpleAction(
            Res.string.browser_add_to_bookmarks.asStringSource(),
            MyIcons.add,
        ) {
            promptAddBookmark(
                BrowserBookmark(
                    url = url,
                    title = title.orEmpty(),
                )
            )
        }
    }

    fun createRemoveFromBookmarkAction(
        url: String,
    ): AnAction {
        return simpleAction(
            Res.string.browser_remove_from_bookmarks.asStringSource(),
            MyIcons.remove,
        ) {
            removeBookmark(url)
        }
    }

    fun createCloseTabAction(tab: ABDMBrowserTab): AnAction {
        return simpleAction(
            title = Res.string.browser_close_tab.asStringSource(),
            icon = MyIcons.close,
        ) {
            closeTab(tab.tabId)
        }
    }

    fun createShowBookmarksAction(): AnAction {
        return simpleAction(
            title = Res.string.browser_bookmarks.asStringSource(),
            icon = MyIcons.hearth,
        ) {
            setShowBookmarkList(true)
        }
    }

    sealed interface Effects {
        data class StartActivity(
            val intent: Intent
        ) : Effects

        data class ShareText(
            val text: String,
        ) : Effects
    }
}

typealias ABDMBrowserTabId = String

@Stable
data class ABDMBrowserTab(
    val tabId: ABDMBrowserTabId,
    val tabState: WebViewState,
) {
    companion object {
        val blankPage = "about:blank"
    }
}

@Stable
data class ABDMTabs(
    val tabs: List<ABDMBrowserTab>,
    val activeTabIndex: Int,
) {
    val tabsSize = tabs.size
    val activeTab get() = if (activeTabIndex == -1) null else tabs[activeTabIndex]

    companion object {
        fun createDefault(): ABDMTabs = ABDMTabs(
            listOf(),
            -1,
        )
    }
}

