package grab.bit.downloader.connection

interface UserAgentProvider {
    fun getUserAgent(): String?
}
