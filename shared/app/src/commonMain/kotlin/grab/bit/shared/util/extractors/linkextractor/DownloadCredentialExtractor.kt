package grab.bit.shared.util.extractors.linkextractor

import grab.bit.shared.util.extractors.Extractor
import grab.bit.downloader.downloaditem.IDownloadCredentials


interface DownloadCredentialExtractor<T> : Extractor<T, List<IDownloadCredentials>> {
    override fun extract(input: T): List<IDownloadCredentials>
}

