package grab.bit.shared.util.extractors.linkextractor

import grab.bit.downloader.downloaditem.IDownloadCredentials


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
        return items
    }
}
