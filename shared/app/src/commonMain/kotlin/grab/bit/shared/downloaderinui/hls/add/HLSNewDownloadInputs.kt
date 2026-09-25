package grab.bit.shared.downloaderinui.hls.add

import grab.bit.resources.Res
import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.add.NewDownloadInputs
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.shared.downloaderinui.hls.HLSLinkChecker
import grab.bit.downloader.downloaditem.hls.HLSResponseInfo
import grab.bit.shared.downloaderinui.http.applyToHttpDownload
import grab.bit.shared.ui.configurable.item.FileChecksumConfigurable
import grab.bit.shared.ui.configurable.item.IntConfigurable
import grab.bit.shared.ui.configurable.item.SpeedLimitConfigurable
import grab.bit.shared.ui.configurable.item.StringConfigurable
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.ThreadCountLimitation
import grab.bit.shared.util.FileChecksum
import grab.bit.shared.util.convertPositiveSpeedToHumanReadable
import grab.bit.shared.util.perhostsettings.PerHostSettingsItem
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig
import grab.bit.downloader.downloaditem.DownloadStatus
import grab.bit.downloader.downloaditem.hls.HLSDownloadItem
import grab.bit.downloader.downloaditem.hls.HLSDownloadJobExtraConfig
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs
import grab.bit.util.flow.combineStateFlows
import grab.bit.util.flow.createMutableStateFlowFromStateFlow
import grab.bit.util.flow.mapStateFlow
import grab.bit.util.flow.mapTwoWayStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HLSNewDownloadInputs(
    downloadUiChecker: HLSNewDownloadUIChecker,
    private val sizeAndSpeedUnitProvider: SizeAndSpeedUnitProvider,
    private val scope: CoroutineScope,
) : NewDownloadInputs<
        HLSDownloadItem,
        HLSDownloadCredentials,
        HLSResponseInfo,
        DownloadSize.Duration,
        HLSLinkChecker,
        >(
    newDownloadUiChecker = downloadUiChecker
) {

    //extra settings
    private var threadCount = MutableStateFlow(null as Int?)
    private var speedLimit = MutableStateFlow(0L)
    private var fileChecksum = MutableStateFlow(null as FileChecksum?)
    override val downloadItem: StateFlow<HLSDownloadItem> = combineStateFlows(
        this.credentials,
        this.folder,
        this.name,
        this.downloadSize,
        this.speedLimit,
        this.threadCount,
        this.fileChecksum,
    ) { credentials,
        folder,
        name,
        duration,
        speedLimit,
        threadCount,
        fileChecksum
        ->
        HLSDownloadItem(
            id = -1,
            folder = folder,
            name = name,
            link = credentials.link,
            dateAdded = openedTime,
            startTime = null,
            completeTime = null,
            status = DownloadStatus.Added,
            preferredConnectionCount = threadCount,
            speedLimit = speedLimit,
            fileChecksum = fileChecksum?.toString(),
            duration = duration?.duration,
        ).withCredentials(credentials)
    }
    override val downloadJobConfig: StateFlow<DownloadJobExtraConfig?> = downloadUiChecker.responseInfo.mapStateFlow {
        it?.let {
            HLSDownloadJobExtraConfig(
                hlsManifest = it.hlsManifest
            )
        }
    }

    override fun applyHostSettingsToExtraConfig(extraConfig: PerHostSettingsItem) {
        extraConfig.applyToHttpDownload(
            setUsername = { setCredentials(credentials.value.copy(username = it)) },
            setPassword = { setCredentials(credentials.value.copy(password = it)) },
            setUserAgent = { setCredentials(credentials.value.copy(userAgent = it)) },
            setThreadCount = { threadCount.value = it },
            setSpeedLimit = { speedLimit.value = it }
        )
    }

    override val configurableList = listOf(
        SpeedLimitConfigurable(
            Res.string.download_item_settings_speed_limit.asStringSource(),
            Res.string.download_item_settings_speed_limit_description.asStringSource(),
            backedBy = speedLimit,
            describe = {
                if (it == 0L) Res.string.unlimited.asStringSource()
                else convertPositiveSpeedToHumanReadable(
                    it, sizeAndSpeedUnitProvider.speedUnit.value
                ).asStringSource()
            }
        ),
        FileChecksumConfigurable(
            Res.string.download_item_settings_file_checksum.asStringSource(),
            Res.string.download_item_settings_file_checksum_description.asStringSource(),
            backedBy = fileChecksum,
            describe = {
                if (it == null) {
                    Res.string.download_item_settings_credentials_empty.asStringSource()
                } else {
                    it.toString().asStringSource()
                }
            }
        ),
        IntConfigurable(
            Res.string.settings_download_thread_count.asStringSource(),
            Res.string.settings_download_thread_count_description.asStringSource(),
            backedBy = threadCount.mapTwoWayStateFlow(
                map = {
                    it ?: 0
                },
                unMap = {
                    it.takeIf { it >= 1 }
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
            backedBy = createMutableStateFlowFromStateFlow(
                flow = credentials.mapStateFlow {
                    it.username.orEmpty()
                },
                updater = {
                    setCredentials(credentials.value.copy(username = it.takeIf { it.isNotBlank() }))
                }, scope
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
            backedBy = createMutableStateFlowFromStateFlow(
                flow = credentials.mapStateFlow {
                    it.password.orEmpty()
                },
                updater = {
                    setCredentials(credentials.value.copy(password = it.takeIf { it.isNotBlank() }))
                }, scope
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
        )
    )

    override fun downloadSizeToStringSource(downloadSize: DownloadSize.Duration): StringSource {
        return downloadSize.asStringSource()
    }
}
