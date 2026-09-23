package ir.amirab.downloader.torrent

import org.libtorrent4j.SessionManager
import org.libtorrent4j.swig.torrent_flags_t

actual fun createTorrentSession(): TorrentSession = LibtorrentSession()

// ponytail: thin seam over SessionManager; settings/DHT tuning and alert
// loop arrive in later steps once a device can run it.
class LibtorrentSession : TorrentSession {
    private val manager = SessionManager()

    override fun start() {
        manager.start()
    }

    override fun stop() {
        manager.stop()
    }

    override fun addMagnet(magnet: MagnetLink): Boolean {
        return runCatching {
            manager.download(magnet.toUri(), null, torrent_flags_t())
            true
        }.getOrDefault(false)
    }
}
