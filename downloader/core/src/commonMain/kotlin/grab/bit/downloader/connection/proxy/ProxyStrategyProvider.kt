package grab.bit.downloader.connection.proxy

interface ProxyStrategyProvider {
    fun getProxyStrategyFor(url: String): ProxyStrategy
}
