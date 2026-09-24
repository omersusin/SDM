package grab.bit.shared.settings

import grab.bit.shared.ui.configurable.ConfigurableGroup
import grab.bit.shared.util.BaseComponent
import grab.bit.shared.util.mvi.ContainsEffects
import grab.bit.shared.util.mvi.supportEffects
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

abstract class BaseSettingsComponent(
    context: ComponentContext
) : BaseComponent(
    context
),
    ContainsEffects<BaseSettingsComponent.Effects> by supportEffects() {
    abstract val configurables: StateFlow<List<ConfigurableGroup>>

    sealed interface Effects {
        interface Platform : Effects
    }
}
