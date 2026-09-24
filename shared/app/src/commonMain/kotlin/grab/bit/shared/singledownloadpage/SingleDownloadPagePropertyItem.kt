package grab.bit.shared.singledownloadpage

import androidx.compose.runtime.Immutable
import grab.bit.util.compose.StringSource

@Immutable
data class SingleDownloadPagePropertyItem(
    val name: StringSource,
    val value: StringSource,
    val valueState: ValueType = ValueType.Normal,
) {
    enum class ValueType { Normal, Error, Success }
}
