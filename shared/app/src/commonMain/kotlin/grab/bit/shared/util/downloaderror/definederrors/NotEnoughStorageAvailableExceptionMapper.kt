package grab.bit.shared.util.downloaderror.definederrors

import grab.bit.resources.Res
import grab.bit.shared.util.SizeAndSpeedUnitProvider
import grab.bit.shared.util.convertPositiveSizeToHumanReadable
import grab.bit.shared.util.downloaderror.DownloadErrorReason
import grab.bit.shared.util.downloaderror.DownloadErrorMapper
import grab.bit.shared.util.downloaderror.DownloadErrorMapper.Companion.createErrorReason
import grab.bit.downloader.exception.NoSpaceInStorageException
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotEnoughStorageAvailableExceptionMapper : DownloadErrorMapper, KoinComponent {
    private val sizeUnitProvider: SizeAndSpeedUnitProvider by inject()
    override fun accept(throwable: Throwable): Boolean {
        return throwable is NoSpaceInStorageException
    }

    override fun getReason(throwable: Throwable): DownloadErrorReason {
        throwable as NoSpaceInStorageException
        val required = convertPositiveSizeToHumanReadable(
            throwable.required,
            sizeUnitProvider.sizeUnit.value,
        ).getString()
        val available = convertPositiveSizeToHumanReadable(
            throwable.available,
            sizeUnitProvider.sizeUnit.value,
        ).getString()

        return createErrorReason(
            title = Res.string.download_error_reason_no_space_title.asStringSource().getString(),
            description = Res.string.download_error_reason_no_space_description.asStringSourceWithARgs(
                Res.string.download_error_reason_no_space_description_createArgs(
                    required = required,
                    available = available,
                )
            ).getString(),
            suggestion = Res.string.download_error_reason_no_space_suggestion.asStringSource().getString(),
            throwable = throwable,
        )
    }
}
