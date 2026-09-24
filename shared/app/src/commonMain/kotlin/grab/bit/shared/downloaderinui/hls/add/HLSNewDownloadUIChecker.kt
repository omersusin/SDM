package grab.bit.shared.downloaderinui.hls.add

import grab.bit.shared.downloaderinui.DownloadSize
import grab.bit.shared.downloaderinui.add.NewDownloadUiChecker
import grab.bit.shared.downloaderinui.LinkCheckerFactory
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.shared.downloaderinui.hls.HLSLinkChecker
import grab.bit.downloader.downloaditem.hls.HLSResponseInfo
import grab.bit.shared.util.DownloadSystem
import kotlinx.coroutines.CoroutineScope

class HLSNewDownloadUIChecker(
    initCredentials: HLSDownloadCredentials,
    linkCheckerFactory: LinkCheckerFactory<HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration, HLSLinkChecker>,
    initialFolder: String,
    initialName: String,
    downloadSystem: DownloadSystem,
    scope: CoroutineScope,
) : NewDownloadUiChecker<HLSDownloadCredentials, HLSResponseInfo, DownloadSize.Duration, HLSLinkChecker>(
    initialCredentials = initCredentials,
    linkCheckerFactory = linkCheckerFactory,
    initialFolder = initialFolder,
    initialName = initialName,
    downloadSystem = downloadSystem,
    scope = scope,
) {
}
