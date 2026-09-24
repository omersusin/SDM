package grab.bit.downloader.torrent

import org.libtorrent4j.SessionManager
import org.libtorrent4j.Sha1Hash
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

    override fun progress(infoHashHex: String): TorrentProgress? {
        return runCatching {
            manager.find(Sha1Hash.parseHex(infoHashHex))?.status()?.progress()?.let(::TorrentProgress)
        }.getOrNull()
    }

    override fun pauseAll() {
        runCatching { manager.pause() }
    }

    override fun resumeAll() {
        runCatching { manager.resume() }
    }

    override fun pause(infoHashHex: String): Boolean {
        return runCatching {
            val handle = manager.find(Sha1Hash.parseHex(infoHashHex)) ?: return false
            handle.pause()
            true
        }.getOrDefault(false)
    }

    override fun resume(infoHashHex: String): Boolean {
        return runCatching {
            val handle = manager.find(Sha1Hash.parseHex(infoHashHex)) ?: return false
            handle.resume()
            true
        }.getOrDefault(false)
    }
}
