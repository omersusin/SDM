package grab.bit.downloader.downloaditem.hls

import io.lindstrom.m3u8.model.MediaPlaylist
import grab.bit.downloader.downloaditem.DownloadJobExtraConfig

data class HLSDownloadJobExtraConfig(
    val hlsManifest: MediaPlaylist? = null
) : DownloadJobExtraConfig
