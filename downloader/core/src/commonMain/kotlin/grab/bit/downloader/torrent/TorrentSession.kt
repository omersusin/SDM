package grab.bit.downloader.torrent

interface TorrentSession {
    fun start()
    fun stop()
    fun addMagnet(magnet: MagnetLink): Boolean
    fun progress(infoHashHex: String): TorrentProgress?
    fun pauseAll()
    fun resumeAll()
    fun pause(infoHashHex: String): Boolean
    fun resume(infoHashHex: String): Boolean
}

data class TorrentProgress(
    val progress: Float,
)

expect fun createTorrentSession(saveDir: String): TorrentSession
