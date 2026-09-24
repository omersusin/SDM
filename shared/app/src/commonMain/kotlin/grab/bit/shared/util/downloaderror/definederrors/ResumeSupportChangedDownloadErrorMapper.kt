package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.exception.ServerPartIsNotTheSameAsWeExpectException
import grab.bit.downloader.exception.ServerResumeSupportChangeException
import grab.bit.util.compose.asStringSource

object ResumeSupportChangedDownloadErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return throwable is ServerResumeSupportChangeException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        return createErrorReason(
            title = Res.string.download_error_reason_server_resume_change_title.asStringSource().getString(),
            description = Res.string.download_error_reason_server_resume_change_description.asStringSource()
                .getString(),
            suggestion = Res.string.download_error_reason_server_resume_change_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
