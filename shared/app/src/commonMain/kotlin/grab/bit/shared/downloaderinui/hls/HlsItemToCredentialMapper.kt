package grab.bit.shared.downloaderinui.hls

import grab.bit.shared.downloaderinui.CredentialAndItemMapper
import grab.bit.downloader.downloaditem.hls.HLSDownloadCredentials
import grab.bit.downloader.downloaditem.hls.HLSDownloadItem

class HlsItemToCredentialMapper : CredentialAndItemMapper<
        HLSDownloadCredentials, HLSDownloadItem> {
    override fun itemToCredentials(item: HLSDownloadItem): HLSDownloadCredentials {
        return HLSDownloadCredentials(
            link = item.link,
            downloadPage = item.downloadPage,
            headers = item.headers,
            username = item.username,
            password = item.password,
            userAgent = item.userAgent,
        )
    }

    override fun appliedCredentialsToItem(
        item: HLSDownloadItem,
        credentials: HLSDownloadCredentials
    ): HLSDownloadItem {
        return item.copy().withCredentials(credentials)
    }

    override fun itemWithEditedName(
        item: HLSDownloadItem,
        name: String
    ): HLSDownloadItem {
        return item.copy(name = name)
    }

    override fun credentialsWithEditedLink(
        credentials: HLSDownloadCredentials,
        link: String
    ): HLSDownloadCredentials {
        return credentials.copy(link = link)
    }

}
