package grab.bit.shared.pages.enterurl

import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.shared.downloaderinui.TADownloaderInUI
import grab.bit.shared.util.ClipboardUtil
import grab.bit.shared.util.extractors.linkextractor.DownloadCredentialsFromSimpleLink
import grab.bit.shared.util.BaseComponent
import grab.bit.shared.util.extractors.linkextractor.StringUrlExtractor
import grab.bit.shared.util.mvi.ContainsEffects
import grab.bit.shared.util.mvi.supportEffects
import com.arkivanov.decompose.ComponentContext
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.util.flow.combineStateFlows
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseEnterNewURLComponent(
    val ctx: ComponentContext,
    val config: Config,
    val downloaderInUiRegistry: DownloaderInUiRegistry,
    private val onCloseRequest: () -> Unit,
    private val onRequestFinished: (IDownloadCredentials) -> Unit,
) : BaseComponent(ctx),
    ContainsEffects<BaseEnterNewURLComponent.Effects> by supportEffects() {
    private val _url: MutableStateFlow<String> = MutableStateFlow("")
    val url = _url.asStateFlow()

    val bestDownloader = url.mapStateFlow {
        downloaderInUiRegistry.bestMatchForThisLink(it)
    }
    private val _downloaderSelection = MutableStateFlow<DownloaderSelection>(
        DownloaderSelection.Auto
    )

    val downloaderSelection = _downloaderSelection.asStateFlow()
    fun selectDownloader(downloaderSelection: DownloaderSelection) {
        _downloaderSelection.value = downloaderSelection
    }


    val downloaderToPickup: StateFlow<TADownloaderInUI?> = combineStateFlows(
        bestDownloader,
        downloaderSelection,
    ) { bestDownloader, userSelection ->
        when (userSelection) {
            DownloaderSelection.Auto -> bestDownloader
            is DownloaderSelection.Fixed -> userSelection.downloaderInUi
        }
    }
    val canAdd = combineStateFlows(
        downloaderToPickup,
        url,
    ) { downloader, url ->
        url.isNotBlank() && downloader != null
    }


    fun setURL(url: String) {
        _url.value = url
    }

    var firstTimeOpened = false
    open val shouldFillWithClipboard = true
    fun onPageOpen() {
        if (!firstTimeOpened) {
            if (shouldFillWithClipboard) {
                scope.launch {
                    if (fillLinkIfThereIsALinkInClipboard()) {
                        sendEffect(Effects.LinkSelectAll)
                    }
                }
            }
            firstTimeOpened = true
        }
    }

    private fun fillLinkIfThereIsALinkInClipboard(): Boolean {
        val possibleLinks = ClipboardUtil.read() ?: return false
        val downloadLinks = StringUrlExtractor.extract(possibleLinks)
        if (downloadLinks.size == 1) {
            setURL(downloadLinks.first())
            return true
        }
        return false
    }


    fun close() {
        scope.launch {
            onCloseRequest()
        }
    }

    fun newDownloadEntered() {
        val downloader = downloaderToPickup.value ?: return
        val link = url.value
        onRequestFinished(
            downloader.createMinimumCredentials(link)
        )
        onCloseRequest()
    }

    val possibleValues = buildList {
        add(DownloaderSelection.Auto)
        addAll(downloaderInUiRegistry.getAll().map {
            DownloaderSelection.Fixed(it)
        })
    }

    interface Config

    sealed interface Effects {
        data object LinkSelectAll : Effects
        interface PlatformEffects : Effects
    }
}
