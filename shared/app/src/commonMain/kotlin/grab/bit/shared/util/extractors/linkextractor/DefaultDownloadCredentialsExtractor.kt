package grab.bit.shared.util.extractors.linkextractor

import grab.bit.downloader.downloaditem.IDownloadCredentials
import grab.bit.util.HttpUrlUtils


object DefaultDownloadCredentialsExtractor :
    DownloadCredentialExtractor<String> {
    override fun extract(input: String): List<IDownloadCredentials> {
        val stringExtractors = listOf(
            DownloadCredentialsFromJson,
            DownloadCredentialsFromCurl,
            DownloadCredentialsFromSimpleLink,
        )
        val items = stringExtractors.firstNotNullOfOrNull { extractor ->
            runCatching {
                extractor
                    .extract(input)
                    .takeIf { it.isNotEmpty() }
            }.getOrElse { null }
        }?.distinctBy { it.link } ?: emptyList()
        // Drop non-downloadable links (file://, javascript:, ...). Private
        // LAN hosts stay allowed: NAS downloads are a legit use case.
        return items.filter { HttpUrlUtils.isValidUrl(it.link) }
    }
}
