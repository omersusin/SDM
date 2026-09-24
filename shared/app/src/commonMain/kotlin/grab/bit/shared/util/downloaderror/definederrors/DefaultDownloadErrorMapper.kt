package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.exception.FileChangedException
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs

object DefaultDownloadErrorMapper : DownloadErrorMapper {

    override fun accept(throwable: Throwable): Boolean {
        return true
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        val exceptionName = throwable::class.simpleName
            ?: Res.string.unknown.asStringSource().getString()
        val message = throwable.localizedMessage
            ?: Res.string.unknown.asStringSource().getString()
        return createErrorReason(
            title = Res.string.download_error_reason_default_title.asStringSource().getString(),
            description = Res.string.download_error_reason_default_description.asStringSourceWithARgs(
                Res.string.download_error_reason_default_description_createArgs(
                    exceptionName = exceptionName,
                    exceptionMessage = message,
                )
            ).getString(),
            suggestion = Res.string.download_error_reason_default_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
