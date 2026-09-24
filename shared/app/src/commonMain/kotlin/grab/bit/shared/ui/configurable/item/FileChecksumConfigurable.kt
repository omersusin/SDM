package grab.bit.shared.ui.configurable.item

import grab.bit.shared.ui.configurable.Configurable
import grab.bit.shared.util.FileChecksum
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FileChecksumConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<FileChecksum?>,
    describe: (FileChecksum?) -> StringSource,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<FileChecksum?>(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = describe,
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key
}

