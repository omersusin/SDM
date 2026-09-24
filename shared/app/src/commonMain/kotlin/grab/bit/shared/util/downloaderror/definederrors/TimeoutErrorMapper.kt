package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.util.compose.asStringSource
import java.net.SocketTimeoutException

object TimeoutErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return throwable is SocketTimeoutException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        return createErrorReason(
            title = Res.string.download_error_reason_timeout_title.asStringSource().getString(),
            description = Res.string.download_error_reason_timeout_description.asStringSource().getString(),
            suggestion = Res.string.download_error_reason_timeout_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
