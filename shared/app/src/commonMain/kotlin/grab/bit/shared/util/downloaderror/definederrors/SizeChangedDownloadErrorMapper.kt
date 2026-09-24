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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SizeChangedDownloadErrorMapper : DownloadErrorMapper, KoinComponent {
    private val sizeAndSpeedUnitProvider: SizeAndSpeedUnitProvider by inject()
    override fun accept(throwable: Throwable): Boolean {
        return throwable is FileChangedException.LengthChangedException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        throwable as FileChangedException.LengthChangedException

        val oldSize =
            convertPositiveSizeToHumanReadable(
                throwable.lastContentLength,
                sizeAndSpeedUnitProvider.sizeUnit.value,
            )
        val newSize = convertPositiveSizeToHumanReadable(
            throwable.newContentLength,
            sizeAndSpeedUnitProvider.sizeUnit.value,
        )
        return createErrorReason(
            title = Res.string.download_error_reason_changed_size_title.asStringSource().getString(),
            description = Res.string.download_error_reason_changed_size_description.asStringSourceWithARgs(
                Res.string.download_error_reason_changed_size_description_createArgs(
                    oldSize = oldSize.getString(),
                    newSize = newSize.getString(),
                )
            ).getString(),
            suggestion = Res.string.download_error_reason_changed_size_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
