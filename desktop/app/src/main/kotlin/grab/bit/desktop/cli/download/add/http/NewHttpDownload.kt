package grab.bit.desktop.cli.download.add.http

import grab.bit.desktop.cli.download.add.shared.BaseNewDownload
import grab.bit.integration.model.HttpDownloadCredentialsFromIntegration
import grab.bit.integration.model.IDownloadCredentialsFromIntegration

class NewHttpDownload : BaseNewDownload("http") {
    val link by linkOption()
    val headers by headersOption()

    override fun createDownload(): IDownloadCredentialsFromIntegration {
        return HttpDownloadCredentialsFromIntegration(
            link = link,
            headers = headers.toMap(),
            downloadPage = downloadPage,
        )
    }
}
