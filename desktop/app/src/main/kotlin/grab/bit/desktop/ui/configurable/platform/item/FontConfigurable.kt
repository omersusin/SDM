package grab.bit.desktop.ui.configurable.platform.item

import grab.bit.desktop.pages.settings.FontInfo
import grab.bit.shared.ui.configurable.BaseEnumConfigurable
import grab.bit.shared.ui.configurable.Configurable
import grab.bit.util.compose.StringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FontConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<FontInfo>,
    describe: (FontInfo) -> StringSource,
    possibleValues: List<FontInfo>,
    valueToString: (FontInfo) -> List<String> = {
        listOf(it.name.getString())
    },
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : BaseEnumConfigurable<FontInfo>(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = describe,
    possibleValues = possibleValues,
    valueToString = valueToString,
    enabled = enabled,
    visible = visible,
) {
    object Key : Configurable.Key

    override fun getKey() = Key
}
