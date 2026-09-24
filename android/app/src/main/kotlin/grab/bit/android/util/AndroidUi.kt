package grab.bit.android.util

import grab.bit.shared.ui.theme.ThemeManager
import grab.bit.util.compose.localizationmanager.LanguageManager
import grab.bit.util.guardedEntry
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AndroidUi : KoinComponent {
    val themeManager: ThemeManager by inject()
    val languageManager: LanguageManager by inject()
    private var booted = guardedEntry()
    fun boot() {
        booted.action {
            themeManager.boot()
            languageManager.boot()
        }
    }
}
