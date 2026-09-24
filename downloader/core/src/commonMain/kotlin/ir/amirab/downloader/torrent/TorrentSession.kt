package ir.amirab.downloader.torrent

interface TorrentSession {
    fun start()
    fun stop()
    fun addMagnet(magnet: MagnetLink): Boolean
    fun progress(infoHashHex: String): TorrentProgress?
    fun pauseAll()
    fun resumeAll()
}

data class TorrentProgress(
    val progress: Float,
)

expect fun createTorrentSession(): TorrentSession
