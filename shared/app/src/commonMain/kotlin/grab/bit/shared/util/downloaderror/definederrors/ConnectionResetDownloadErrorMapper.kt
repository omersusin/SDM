package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs
import java.net.SocketException

object ConnectionResetDownloadErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return (throwable is SocketException && throwable.message?.contains("Connection reset") == true)
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        return createErrorReason(
            title = Res.string.download_error_reason_connection_reset_title.asStringSource().getString(),
            description = Res.string.download_error_reason_connection_reset_description.asStringSource().getString(),
            suggestion = Res.string.download_error_reason_connection_reset_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
