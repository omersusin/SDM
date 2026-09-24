package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.exception.PrepareDestinationFailedException
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs

object DestinationExceptionDownloadErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return (throwable is PrepareDestinationFailedException)
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        val causeClassName: String
        val causeMessage: String
        val cause = throwable.cause
        if (cause != null) {
            causeClassName = cause::class.simpleName ?: Res.string.unknown.asStringSource().getString()
            causeMessage = cause.localizedMessage ?: Res.string.unknown.asStringSource().getString()
        } else {
            val unknown = Res.string.unknown.asStringSource().getString()
            causeClassName = unknown
            causeMessage = unknown
        }
        return createErrorReason(
            title = Res.string.download_error_reason_destination_title.asStringSource().getString(),
            description = Res.string.download_error_reason_destination_description.asStringSourceWithARgs(
                Res.string.download_error_reason_destination_description_createArgs(
                    exceptionName = causeClassName,
                    exceptionMessage = causeMessage,
                )
            ).getString(),
            suggestion = Res.string.download_error_reason_destination_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
