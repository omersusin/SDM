package grab.bit.android.pages.browser

import android.webkit.CookieManager
import grab.bit.android.ui.widget.WebViewState
import grab.bit.shared.downloaderinui.http.applyToHttpDownload
import grab.bit.shared.pages.adddownload.AddDownloadCredentialsInUiProps
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import grab.bit.shared.util.perhostsettings.getSettingsForURL
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.util.HttpUrlUtils
import grab.bit.util.AdBlockMatcher
import grab.bit.util.MediaCandidate
import grab.bit.util.PageMediaCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

typealias ABDMWebRequestId = String

data class ABDMWebRequest(
    val url: String,
    val headers: Map<String, String>,
    val page: String?,
) {
    val id: ABDMWebRequestId = url
}

interface RequestInterceptor {
    fun interceptRequest(request: ABDMWebRequest)
}

class DownloadInterceptor(
    private val scope: CoroutineScope,
    private val perHostSettingsManager: PerHostSettingsManager?,
    private val onNewDownload: (newDownloads: List<AddDownloadCredentialsInUiProps>) -> Unit,
) : RequestInterceptor {
    private val requests = mutableMapOf<String, ABDMWebRequest>()
    private val mediaByPage = mutableMapOf<String, PageMediaCollector>()
    private val _mediaCounts = MutableStateFlow(emptyMap<String, Int>())
    val mediaCounts: StateFlow<Map<String, Int>> = _mediaCounts.asStateFlow()

    // Set once the filter list is loaded (bundled asset / update in later step).
    @Volatile
    var adBlock: AdBlockMatcher? = null

    @Volatile
    var adBlockEnabled: Boolean = true

    fun isAdBlocked(url: String): Boolean {
        if (!adBlockEnabled) return false
        return adBlock?.isBlocked(url) ?: false
    }

    fun mediaForPage(page: String): List<MediaCandidate> {
        return mediaByPage[page]?.snapshot().orEmpty()
    }

    fun onDownloadStart(
        url: String?,
        userAgent: String?,
        page: String?,
        tab: ABDMBrowserTab,
    ) {
        if (url == null) {
            return
        }
        if (!HttpUrlUtils.isValidUrl(url)) {
            return
        }
        val webRequest = getWebRequestOrDefault(
            url = url,
            userAgent = userAgent,
            page = page,
            webViewState = tab.tabState,
        )
        onNewDownload(
            listOf(
                AddDownloadCredentialsInUiProps(
                    perHostSettingsManager?.getSettingsForURL(url)?.applyToHttpDownload(
                        HttpDownloadCredentials(
                            link = webRequest.url,
                            headers = webRequest.headers,
                            downloadPage = webRequest.page,
                        )
                    ) ?: HttpDownloadCredentials(
                        link = webRequest.url,
                        headers = webRequest.headers,
                        downloadPage = webRequest.page,
                    ),
                    AddDownloadCredentialsInUiProps.Configs()
                )
            )
        )
    }

    override fun interceptRequest(
        request: ABDMWebRequest,
    ) {
        addToHeaders(request)
        request.page?.let { page ->
            val collector = mediaByPage.getOrPut(page) { PageMediaCollector() }
            collector.observe(request.url)
            _mediaCounts.update { it + (page to collector.count) }
        }
    }

    private fun addToHeaders(request: ABDMWebRequest) {
        requests[request.id] = request
        scope.launch {
            delay(REMOVE_REQUESTS_DELAY.milliseconds)
            requests.remove(request.id)
        }
    }

    private fun getWebRequestOrDefault(
        url: String,
        userAgent: String?,
        page: String?,
        webViewState: WebViewState,
    ): ABDMWebRequest {
        var request = requests[url]
        if (request == null) {
            request = ABDMWebRequest(
                url = url,
                headers = emptyMap(),
                page = getPageUrl(webViewState) ?: page,
            )
        }
        return request
            .stripCrossOriginHeaders(url)
            .withUserAgent(userAgent)
            .withCookieManagerCookies()
    }

    // Page-captured headers (Cookie/Authorization/Referer) must not leak
    // to third-party hosts (ads/trackers/CDNs). Same host keeps all.
    private fun ABDMWebRequest.stripCrossOriginHeaders(downloadUrl: String): ABDMWebRequest {
        val pageHost = page?.let { HttpUrlUtils.getHost(it) } ?: return this
        val downloadHost = HttpUrlUtils.getHost(downloadUrl) ?: return this
        if (pageHost.equals(downloadHost, ignoreCase = true)) {
            return this
        }
        return copy(
            headers = headers.filterKeys { key ->
                key.equals("User-Agent", ignoreCase = true) ||
                    key.equals("Range", ignoreCase = true) ||
                    key.equals("Accept", ignoreCase = true) ||
                    key.startsWith("Accept-", ignoreCase = true)
            }
        )
    }

    private fun ABDMWebRequest.withUserAgent(userAgent: String?): ABDMWebRequest {
        val request = this
        if (userAgent == null) {
            return request
        }
        val userAgentKey = "User-Agent"
        if (request.headers.containsKey(userAgentKey)) {
            return request
        }
        return request.copy(
            headers = request.headers.plus(
                userAgentKey to userAgent
            )
        )
    }

    private fun ABDMWebRequest.withCookieManagerCookies(): ABDMWebRequest {
        val request = this
        val cookieFromCookieManager =
            CookieManager.getInstance().getCookie(url)?.takeIf { it.isNotBlank() } ?: return request
        val cookieKey = "Cookie"
        val currentCookie = request.headers[cookieKey]?.takeIf { it.isNotBlank() }
        return request.copy(
            headers = request.headers.plus(
                cookieKey to if (currentCookie != null) {
                    "$currentCookie; $cookieFromCookieManager"
                } else {
                    cookieFromCookieManager
                }
            )
        )
    }

    private fun getPageUrl(state: WebViewState): String? {
        return state.lastLoadedUrl
    }

    companion object {
        private const val REMOVE_REQUESTS_DELAY = 20_000L
    }
}
