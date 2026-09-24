package grab.bit.android.pages.onboarding.initialsetup

import grab.bit.shared.settings.CommonSettings
import grab.bit.shared.ui.configurable.ConfigurableGroup
import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.shared.util.BaseComponent
import com.arkivanov.decompose.ComponentContext
import grab.bit.util.compose.localizationmanager.LanguageManager

class InitialSetupComponent(
    ctx: ComponentContext,
    private val languageManager: LanguageManager,
    private val themeManager: ThemeManager,
    private val onFinish: () -> Unit
) : BaseComponent(ctx) {
    val configurables = listOf(
            CommonSettings.languageConfig(languageManager, scope),
            CommonSettings.themeConfig(themeManager, scope),
        )

    fun onUserPressFinish() {
        onFinish()
    }
}
