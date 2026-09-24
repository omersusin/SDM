package grab.bit.shared.downloaderinui.edit

import grab.bit.resources.Res
import grab.bit.shared.util.convertDurationToHumanReadable
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.asStringSourceWithARgs

sealed interface CanEditWarnings {
    fun asStringSource(): StringSource
    data class FileSizeNotMatch(
        val currentSize: Long,
        val newSize: Long,
    ) : CanEditWarnings {
        override fun asStringSource(): StringSource {
            return Res.string.edit_download_saved_download_item_size_not_match
                .asStringSourceWithARgs(
                    Res.string.edit_download_saved_download_item_size_not_match_createArgs(
                        currentSize = "$currentSize",
                        newSize = "$newSize",
                    )
                )
        }

    }
    data class DurationNotMatch(
        val currentDuration: Double?,
        val newDuration: Double?,
    ) : CanEditWarnings {
        val notAvailableString = Res.string.unknown.asStringSource()
        override fun asStringSource(): StringSource {
            val currentDurationString = currentDuration?.let {
                convertDurationToHumanReadable(it)
            } ?: notAvailableString
            val newDurationString = newDuration?.let {
                convertDurationToHumanReadable(it)
            } ?: notAvailableString
            return Res.string.edit_download_saved_download_item_size_not_match
                .asStringSourceWithARgs(
                    Res.string.edit_download_saved_download_item_size_not_match_createArgs(
                        currentSize = currentDurationString.getString(),
                        newSize = newDurationString.getString(),
                    )
                )
        }

    }
}
