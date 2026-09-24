package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.convertPositiveSizeToHumanReadable
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.exception.FileChangedException
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs

object ChangedToWebPageDownloadErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return throwable is FileChangedException.GotAWebPage
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        return createErrorReason(
            title = Res.string.download_error_reason_changed_web_title.asStringSource().getString(),
            description = Res.string.download_error_reason_changed_web_description.asStringSource().getString(),
            suggestion = Res.string.download_error_reason_changed_web_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
