package grab.bit.desktop.cli.download.add.hls

import grab.bit.desktop.cli.download.add.shared.BaseNewDownload
import grab.bit.integration.model.HLSDownloadCredentialsFromIntegration
import grab.bit.integration.model.IDownloadCredentialsFromIntegration

class NewHlsDownload : BaseNewDownload("hls") {

    val link by linkOption()
    val headers by headersOption()


    override fun createDownload(): IDownloadCredentialsFromIntegration {
        return HLSDownloadCredentialsFromIntegration(
            link = link,
            headers = headers.toMap(),
            downloadPage = downloadPage,
        )
    }
}
