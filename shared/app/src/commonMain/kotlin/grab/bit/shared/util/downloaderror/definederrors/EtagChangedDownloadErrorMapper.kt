package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.downloader.exception.FileChangedException
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs

object EtagChangedDownloadErrorMapper : DownloadErrorMapper {
    override fun accept(throwable: Throwable): Boolean {
        return throwable is FileChangedException.ETagChangedException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        throwable as FileChangedException.ETagChangedException
        val oldEtag = throwable.oldETag
        val newEtag = throwable.newETag
        return createErrorReason(
            title = Res.string.download_error_reason_changed_etag_title.asStringSource().getString(),
            description = Res.string.download_error_reason_changed_etag_description.asStringSourceWithARgs(
                Res.string.download_error_reason_changed_etag_description_createArgs(
                    oldETag = oldEtag,
                    newETag = newEtag,
                )
            ).getString(),
            suggestion = Res.string.download_error_reason_changed_etag_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
