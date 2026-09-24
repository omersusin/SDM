package grab.bit.shared.downloaderinui.http

import grab.bit.shared.downloaderinui.CredentialAndItemMapper
import grab.bit.downloader.downloaditem.http.HttpDownloadCredentials
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import grab.bit.downloader.downloaditem.http.withHttpCredentials

object HttpCredentialsToItemMapper : CredentialAndItemMapper<HttpDownloadCredentials, HttpDownloadItem> {
    override fun itemToCredentials(item: HttpDownloadItem): HttpDownloadCredentials {
        return HttpDownloadCredentials.from(item)
    }

    override fun appliedCredentialsToItem(
        item: HttpDownloadItem,
        credentials: HttpDownloadCredentials
    ): HttpDownloadItem {
        return item.copy().withHttpCredentials(credentials)
    }

    override fun itemWithEditedName(item: HttpDownloadItem, name: String): HttpDownloadItem {
        return item.copy(name = name)
    }

    override fun credentialsWithEditedLink(
        credentials: HttpDownloadCredentials,
        link: String
    ): HttpDownloadCredentials {
        return credentials.copy(link = link)
    }

}
