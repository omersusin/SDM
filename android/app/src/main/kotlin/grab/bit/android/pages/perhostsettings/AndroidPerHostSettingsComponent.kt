package grab.bit.android.pages.perhostsettings

import grab.bit.shared.pages.perhostsettings.BasePerHostSettingsComponent
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.util.perhostsettings.PerHostSettingsManager
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable

class AndroidPerHostSettingsComponent(
    ctx: ComponentContext,
    perHostSettingsManager: PerHostSettingsManager,
    appRepository: BaseAppRepository,
    appScope: CoroutineScope,
    closeRequested: () -> Unit,
) : BasePerHostSettingsComponent(
    ctx = ctx,
    perHostSettingsManager = perHostSettingsManager,
    appRepository = appRepository,
    appScope = appScope,
    closeRequested = closeRequested,
) {
    @Serializable
    data class Config(
        override val openedHost: String?
    ) : BasePerHostSettingsComponent.Config

    sealed interface Effects : BasePerHostSettingsComponent.Effects.Platform {
    }

    fun reset() {
        editedPerHostSettings.value = savedPerHostSettings.value
        onIdSelected(null)
    }

    fun saveAndReturn() {
        save()
        onIdSelected(null)
    }
}
