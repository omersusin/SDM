package grab.bit.shared.util.extractors.linkextractor

import grab.bit.shared.downloaderinui.DownloaderInUiRegistry
import grab.bit.downloader.downloaditem.IDownloadCredentials
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DownloadCredentialsFromSimpleLink :
    DownloadCredentialExtractor<String>, KoinComponent {
    val downloaderInUiRegistry: DownloaderInUiRegistry by inject()
    override fun extract(input: String): List<IDownloadCredentials> {
        return StringUrlExtractor.extract(input)
            .mapNotNull {
                downloaderInUiRegistry
                    .bestMatchForThisLink(it)
                    ?.createMinimumCredentials(it)
            }
    }
}
