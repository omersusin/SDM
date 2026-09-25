package grab.bit.shared.downloaderinui.hls.edit

import grab.bit.resources.Res
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkCheckerFactory
import grab.bit.shared.downloaderinui.edit.DownloadConflictDetector
import grab.bit.shared.downloaderinui.edit.EditDownloadCheckerFactory
import grab.bit.shared.downloaderinui.edit.EditDownloadInputs
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.shared.downloaderinui.hls.HLSLinkChecker
import grab.bit.downloader.downloaditem.hls.HLSResponseInfo
import grab.bit.shared.downloaderinui.hls.HlsItemToCredentialMapper
import grab.bit.shared.ui.configurable.item.FileChecksumConfigurable
import grab.bit.shared.ui.configurable.item.IntConfigurable
import grab.bit.shared.ui.configurable.item.SpeedLimitConfigurable
import grab.bit.shared.ui.configurable.item.StringConfigurable
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.ThreadCountLimitation
import grab.bit.shared.util.FileChecksum
import grab.bit.shared.util.convertPositiveSpeedToHumanReadable
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.hls.HLSDownloadItem
import grab.bit.downloader.downloaditem.hls.HLSDownloadJobExtraConfig
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs
import grab.bit.util.flow.mapStateFlow
import grab.bit.util.flow.mapTwoWayStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HLSEditDownloadInputs(
    currentDownloadItem: MutableStateFlow<HLSDownloadItem>,
    editedDownloadItem: MutableStateFlow<HLSDownloadItem>,
    mapper: HlsItemToCredentialMapper,
    conflictDetector: DownloadConflictDetector,
    linkCheckerFactory: LinkCheckerFactory<HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration, HLSLinkChecker>,
    editDownloadCheckerFactory: EditDownloadCheckerFactory<HLSDownloadItem, HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration, HLSLinkChecker>,
    scope: CoroutineScope,
    private val sizeAndSpeedUnitProvider: SizeAndSpeedUnitProvider,
) : EditDownloadInputs<
        HLSDownloadItem,
        HLSDownloadCredentials,
        HLSResponseInfo,
        DownloadSize.Duration,
        HLSLinkChecker,
        HlsItemToCredentialMapper
        >(
    currentDownloadItem,
    editedDownloadItem,
    mapper = mapper,
    scope = scope,
    conflictDetector = conflictDetector,
    linkCheckerFactory = linkCheckerFactory,
    editDownloadCheckerFactory = editDownloadCheckerFactory,
) {

    override val configurableList = listOf(
        SpeedLimitConfigurable(
            Res.string.download_item_settings_speed_limit.asStringSource(),
            Res.string.download_item_settings_speed_limit_description.asStringSource(),
            backedBy = editedDownloadItem.mapTwoWayStateFlow(
                map = {
                    it.speedLimit
                },
                unMap = {
                    copy(speedLimit = it)
                }
            ),
            describe = {
                if (it == 0L) Res.string.unlimited.asStringSource()
                else convertPositiveSpeedToHumanReadable(it, sizeAndSpeedUnitProvider.speedUnit.value).asStringSource()
            }
        ),
        FileChecksumConfigurable(
            Res.string.download_item_settings_file_checksum.asStringSource(),
            Res.string.download_item_settings_file_checksum_description.asStringSource(),
            backedBy = editedDownloadItem.mapTwoWayStateFlow(
                map = {
                    it.fileChecksum?.let {
                        runCatching {
                            FileChecksum.Companion.fromString(it)
                        }.onFailure {
                            println(it.printStackTrace())
                        }.getOrNull()
                    }
                },
                unMap = {
                    copy(fileChecksum = it?.toString())
                }
            ),
            describe = { "".asStringSource() }
        ),
        IntConfigurable(
            Res.string.settings_download_thread_count.asStringSource(),
            Res.string.settings_download_thread_count_description.asStringSource(),
            backedBy = editedDownloadItem.mapTwoWayStateFlow(
                map = {
                    it.preferredConnectionCount ?: 0
                },
                unMap = {
                    copy(
                        preferredConnectionCount = it.takeIf { it >= 1 }
                    )
                }
            ),
            range = 0..ThreadCountLimitation.MAX_ALLOWED_THREAD_COUNT,
            describe = {
                if (it == 0) Res.string.use_global_settings.asStringSource()
                else Res.string.download_item_settings_thread_count_describe
                    .asStringSourceWithARgs(
                        Res.string.download_item_settings_thread_count_describe_createArgs(
                            count = it.toString()
                        )
                    )
            }
        ),
        StringConfigurable(
            Res.string.username.asStringSource(),
            Res.string.download_item_settings_username_description.asStringSource(),
            backedBy = credentials.mapTwoWayStateFlow(
                map = {
                    it.username.orEmpty()
                },
                unMap = {
                    copy(username = it.takeIf { it.isNotEmpty() })
                }
            ),
            describe = {
                if (it.isBlank()) {
                    Res.string.download_item_settings_credentials_empty.asStringSource()
                } else {
                    it.asStringSource()
                }
            }
        ),
        StringConfigurable(
            Res.string.password.asStringSource(),
            Res.string.download_item_settings_password_description.asStringSource(),
            backedBy = credentials.mapTwoWayStateFlow(
                map = {
                    it.password.orEmpty()
                },
                unMap = {
                    copy(password = it.takeIf { it.isNotEmpty() })
                }
            ),
            secret = true,
            describe = {
                if (it.isBlank()) {
                    Res.string.download_item_settings_credentials_empty.asStringSource()
                } else {
                    "••••••••".asStringSource()
                }
            }
        ),
        StringConfigurable(
            Res.string.download_item_settings_user_agent.asStringSource(),
            Res.string.download_item_settings_user_agent_description.asStringSource(),
            backedBy = credentials.mapTwoWayStateFlow(
                map = {
                    it.userAgent.orEmpty()
                },
                unMap = {
                    copy(userAgent = it.takeIf { it.isNotEmpty() })
                }
            ),
            placeholder = Res.string.settings_default_user_agent_placeholder.asStringSource(),
            describe = {
                if (it.isBlank()) {
                    Res.string.use_global_settings.asStringSource()
                } else {
                    it.take(80).asStringSource()
                }
            }
        ),
        StringConfigurable(
            Res.string.download_item_settings_download_page.asStringSource(),
            Res.string.download_item_settings_download_page_description.asStringSource(),
            backedBy = credentials.mapTwoWayStateFlow(
                map = {
                    it.downloadPage.orEmpty()
                },
                unMap = {
                    copy(downloadPage = it.takeIf { it.isNotEmpty() })
                }
            ),
            describe = {
                if (it.isBlank()) {
                    Res.string.download_item_settings_credentials_empty.asStringSource()
                } else {
                    it.asStringSource()
                }
            }
        ),
    )
    val duration = linkChecker.duration
    override val downloadJobConfig: StateFlow<DownloadJobExtraConfig?> = linkChecker.responseInfo.mapStateFlow {
        it?.let {
            HLSDownloadJobExtraConfig(it.hlsManifest)
        }
    }

    private fun HLSDownloadItem.applyOurChanges(edited: HLSDownloadItem) {
        // we don't change some of these properties, so I commented them

        link = edited.link
        headers = edited.headers
        username = edited.username
        password = edited.password
        downloadPage = edited.downloadPage
        userAgent = edited.userAgent

//        id = edited.id
        folder = edited.folder
        name = edited.name

        contentLength = edited.contentLength

//        dateAdded = edited.dateAdded
//        startTime = edited.startTime
//        completeTime = edited.completeTime
//        status = edited.status
        preferredConnectionCount = edited.preferredConnectionCount
        speedLimit = edited.speedLimit

        fileChecksum = edited.fileChecksum
        duration = edited.duration
    }

    override fun applyEditedItemTo(item: HLSDownloadItem) {
        val edited = editedDownloadItem.value
        item.applyOurChanges(edited)
    }

    init {
        duration.onEach {
            scheduleRefresh(alsoRecheckLink = false)
        }.launchIn(scope)
    }

    override fun downloadSizeToStringSource(downloadSize: DownloadSize.Duration): StringSource {
        return downloadSize.asStringSource()
    }
}
