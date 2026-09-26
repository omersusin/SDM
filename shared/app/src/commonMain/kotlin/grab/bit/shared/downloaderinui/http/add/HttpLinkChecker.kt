package grab.bit.shared.downloaderinui.http.add

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.util.FilenameFixer
import grab.bit.shared.downloaderinui.LinkChecker
import grab.bit.downloader.connection.HttpDownloaderClient
import grab.bit.downloader.connection.response.HttpResponseInfo
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.util.HttpUrlUtils
import grab.bit.util.flow.mapStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HttpLinkChecker(
    initialCredentials: HttpDownloadCredentials = HttpDownloadCredentials.empty(),
    private val client: HttpDownloaderClient,
) : LinkChecker<HttpDownloadCredentials, HttpResponseInfo, DownloadSize.Bytes>(initialCredentials) {
    private val _suggestedName = MutableStateFlow(null as String?)
    override val suggestedName = _suggestedName.asStateFlow()

    private val _length = MutableStateFlow(null as Long?)
    override val downloadSize = _length.mapStateFlow {
        it?.let(DownloadSize::Bytes)
    }

    override fun infoUpdated(responseInfo: HttpResponseInfo?) {
        updateNameAndLength(responseInfo)
    }

    override suspend fun actualCheck(credentials: HttpDownloadCredentials): HttpResponseInfo {
        return client.test(credentials)
    }

    private fun updateNameAndLength(responseInfo: HttpResponseInfo?) {
        val suggestedName = (
            responseInfo?.fileName
                ?: HttpUrlUtils.extractNameFromLink(credentials.value.link)
            )?.let(FilenameFixer::fix)
        val length = responseInfo?.run {
            totalLength.takeIf { isSuccessFul }
        }
        _suggestedName.update { suggestedName }
        _length.update { length }
    }
}
