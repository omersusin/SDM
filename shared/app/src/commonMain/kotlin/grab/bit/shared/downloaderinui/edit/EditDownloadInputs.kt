package grab.bit.shared.downloaderinui.edit

import grab.bit.resources.Res
import grab.bit.shared.downloaderinui.CredentialAndItemMapper
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.shared.downloaderinui.LinkCheckerFactory
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.downloader.connection.IResponseInfo
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.flow.mapStateFlow
import grab.bit.util.flow.mapTwoWayStateFlow
import grab.bit.util.flow.onEachLatest
import grab.bit.util.toSingleLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

typealias TAEditDownloadInputs = EditDownloadInputs<*, *, *, *, *, *>

// if I make it like NewDownloadInputs it becomes more usable and simpler to use
abstract class EditDownloadInputs<
        TDownloadItem : IDownloadItem,
        TCredentials : IDownloadCredentials,
        TResponseInfo : IResponseInfo,
        TDownloadSize : DownloadSize,
        TLinkChecker : LinkChecker<TCredentials, TResponseInfo, TDownloadSize>,
        TCredentialAndItemMapper : CredentialAndItemMapper<TCredentials, TDownloadItem>
        >(
    val currentDownloadItem: MutableStateFlow<TDownloadItem>,
    val editedDownloadItem: MutableStateFlow<TDownloadItem>,
    val mapper: TCredentialAndItemMapper,
    val scope: CoroutineScope,
    val conflictDetector: DownloadConflictDetector,
    linkCheckerFactory: LinkCheckerFactory<TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>,
    editDownloadCheckerFactory: EditDownloadCheckerFactory<TDownloadItem, TCredentials, TResponseInfo, TDownloadSize, TLinkChecker>,
) {
    private val _showMoreSettings = MutableStateFlow(false)
    val showMoreSettings = _showMoreSettings.asStateFlow()
    fun setShowMoreSettings(showMoreSettings: Boolean) {
        _showMoreSettings.value = showMoreSettings
    }

    val credentials: MutableStateFlow<TCredentials> = editedDownloadItem.mapTwoWayStateFlow(
        map = {
            mapper.itemToCredentials(it)
        },
        unMap = {
            mapper.appliedCredentialsToItem(this, it)
        }
    )
    val name = editedDownloadItem.mapTwoWayStateFlow(
        map = {
            it.name
        },
        unMap = {
            mapper.itemWithEditedName(this, it)
        }
    )
    abstract val downloadJobConfig: StateFlow<DownloadJobExtraConfig?>
    abstract val configurableList: List<Configurable<*>>
    abstract fun applyEditedItemTo(item: TDownloadItem)
    fun setName(name: String) {
        val refinedName = name
            .toSingleLine()
        this.name.value = refinedName
    }

    val link = credentials.mapTwoWayStateFlow(
        map = { it.link },
        unMap = {
            mapper.credentialsWithEditedLink(this, it)
        }
    )

    fun setLink(link: String) {
        credentials.update {
            mapper.credentialsWithEditedLink(it, link)
        }
    }

    fun importCredentials(importedCredentials: TCredentials) {
        this.credentials.update {
            importedCredentials
        }
    }

    protected val linkChecker = linkCheckerFactory.createLinkChecker(credentials.value)
    private val editDownloadChecker = editDownloadCheckerFactory.createEditDownloadChecker(
        currentDownloadItem = currentDownloadItem,
        editedDownloadItem = editedDownloadItem,
        linkChecker = linkChecker,
        conflictDetector = conflictDetector,
        scope = scope,
    )

    val isLinkLoading = linkChecker.isLoading

    val gettingResponseInfo = linkChecker.isLoading
    val responseResult = linkChecker.responseResult
    val responseInfo = linkChecker.responseInfo


    val canEditDownloadResult = editDownloadChecker.canEditResult
    val canEdit = editDownloadChecker.canEdit

    private val refreshResponseInfoImmediately = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val scheduleRefreshResponseInfo = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val scheduleRecheckEditDownloadIsPossible = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    fun refresh() {
        refreshResponseInfoImmediately.tryEmit(Unit)
    }

    protected fun scheduleRefresh(
        alsoRecheckLink: Boolean,
    ) {
        if (alsoRecheckLink) {
            scheduleRefreshResponseInfo.tryEmit(Unit)
        }
        scheduleRecheckEditDownloadIsPossible.tryEmit(Unit)
    }

    init {
        merge(
            scheduleRefreshResponseInfo.debounce(500.milliseconds),
            refreshResponseInfoImmediately
        ).onEachLatest {
            linkChecker.check()
        }.launchIn(scope)
        merge(
            scheduleRecheckEditDownloadIsPossible.debounce(500.milliseconds),
//            ...
        ).onEachLatest {
            editDownloadChecker.check()
        }.launchIn(scope)

        credentials.onEach { credentials ->
            linkChecker.credentials.update { credentials }
            scheduleRefresh(alsoRecheckLink = true)
        }.launchIn(scope)
        editedDownloadItem.onEach {
            scheduleRefresh(alsoRecheckLink = false)
        }.launchIn(scope)
    }

    abstract fun downloadSizeToStringSource(downloadSize: TDownloadSize): StringSource
    val lengthStringFlow: StateFlow<StringSource> = linkChecker.downloadSize.mapStateFlow {
        it
            ?.let(::downloadSizeToStringSource)
            ?: Res.string.unknown.asStringSource()
    }

    fun getLengthString(): StringSource {
        return lengthStringFlow.value
    }
}
