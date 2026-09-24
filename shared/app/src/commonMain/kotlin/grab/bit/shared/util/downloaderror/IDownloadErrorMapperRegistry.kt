package grab.bit.shared.util.downloaderror

interface IDownloadErrorMapperRegistry {
    fun getReason(throwable: Throwable): DownloadErrorReason?
}
