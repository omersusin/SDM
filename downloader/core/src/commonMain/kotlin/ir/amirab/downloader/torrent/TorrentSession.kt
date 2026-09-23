package ir.amirab.downloader.torrent

interface TorrentSession {
    fun start()
    fun stop()
    fun addMagnet(magnet: MagnetLink): Boolean
}

expect fun createTorrentSession(): TorrentSession
