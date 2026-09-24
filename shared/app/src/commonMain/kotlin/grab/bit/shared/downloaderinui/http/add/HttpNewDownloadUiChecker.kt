package grab.bit.shared.downloaderinui.http.add

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.downloaderinui.add.NewDownloadUiChecker
import grab.bit.shared.downloaderinui.LinkCheckerFactory
import grab.bit.downloader.connection.response.HttpResponseInfo
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import kotlinx.coroutines.CoroutineScope

class HttpNewDownloadUiChecker(
    initialCredentials: HttpDownloadCredentials = HttpDownloadCredentials.Companion.empty(),
    linkCheckerFactory: LinkCheckerFactory<HttpDownloadCredentials, HttpResponseInfo, DownloadSize.Bytes, HttpLinkChecker>,
    initialFolder: String,
    initialName: String = "",
    downloadSystem: DownloadSystem,
    scope: CoroutineScope,
) : NewDownloadUiChecker<HttpDownloadCredentials, HttpResponseInfo, DownloadSize.Bytes, HttpLinkChecker>(
    initialCredentials, linkCheckerFactory, initialFolder, initialName, downloadSystem, scope
) {
}
