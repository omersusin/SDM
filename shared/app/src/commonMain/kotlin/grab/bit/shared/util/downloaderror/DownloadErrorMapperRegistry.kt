package grab.bit.shared.util.downloaderror

import grab.bit.downloader.exception.TooManyErrorException
import grab.bit.downloader.utils.ExceptionUtils
import grab.bit.util.ifThen


class DownloadErrorMapperRegistry(
    val exceptionMappers: List<DownloadErrorMapper>
) : IDownloadErrorMapperRegistry {

    override fun getReason(throwable: Throwable): DownloadErrorReason? {
        if (ExceptionUtils.isNormalCancellation(throwable)) {
            return null
        }
        val actualThrowable = throwable.ifThen(throwable is TooManyErrorException) {
            throwable.findActualDownloadErrorCause()
        }
        return exceptionMappers
            .asSequence()
            .filter {
                it.accept(actualThrowable)
            }.firstNotNullOfOrNull {
                runCatching {
                    it.getReason(actualThrowable)
                }.getOrNull()
            }
    }
}
