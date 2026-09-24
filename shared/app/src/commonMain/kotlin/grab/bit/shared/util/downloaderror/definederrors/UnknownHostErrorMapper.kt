package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.util.compose.asStringSource
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object UnknownHostErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return throwable is UnknownHostException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        return createErrorReason(
            title = Res.string.download_error_reason_unknown_host_title.asStringSource().getString(),
            description = Res.string.download_error_reason_unknown_host_description.asStringSource().getString(),
            suggestion = Res.string.download_error_reason_unknown_host_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
