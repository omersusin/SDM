package grab.bit.shared.downloaderinui.hls

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.shared.util.FilenameFixer
import grab.bit.downloader.connection.HttpDownloaderClient
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.downloader.downloaditem.hls.HLSResponseInfo
import grab.bit.util.HttpUrlUtils
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HLSLinkChecker(
    credentials: HLSDownloadCredentials,
    private val client: HttpDownloaderClient,
) : LinkChecker<HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration>(
    initialCredentials = credentials
) {
    private val _suggestedName: MutableStateFlow<String?> = MutableStateFlow(null)
    override val suggestedName: StateFlow<String?> = MutableStateFlow(null)
    private val _duration: MutableStateFlow<Double?> = MutableStateFlow(null)
    val duration: StateFlow<Double?> = _duration.asStateFlow()
    override val downloadSize: StateFlow<DownloadSize.Duration?> = _duration.mapStateFlow {
        it?.let(DownloadSize::Duration)
    }
    override fun infoUpdated(responseInfo: HLSResponseInfo?) {
        _suggestedName.value = responseInfo?.name ?: HttpUrlUtils.extractNameFromLink(credentials.value.link)
            ?.let(FilenameFixer::fix)
        _duration.value = responseInfo?.duration
    }

    override suspend fun actualCheck(credentials: HLSDownloadCredentials): HLSResponseInfo {
        return client.connect(credentials, null, null)
            .use { HLSResponseInfo.fromConnection(it) }
    }
}
