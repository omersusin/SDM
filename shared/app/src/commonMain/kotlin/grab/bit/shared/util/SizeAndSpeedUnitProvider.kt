package grab.bit.shared.util

import grab.bit.util.datasize.ConvertSizeConfig
import kotlinx.coroutines.flow.StateFlow

interface SizeAndSpeedUnitProvider {
    val sizeUnit: StateFlow<ConvertSizeConfig>
    val speedUnit: StateFlow<ConvertSizeConfig>
}
