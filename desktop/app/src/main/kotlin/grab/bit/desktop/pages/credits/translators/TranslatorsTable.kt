package grab.bit.desktop.pages.credits.translators

import grab.bit.shared.ui.widget.table.customtable.CellSize
import grab.bit.shared.ui.widget.table.customtable.SortableCell
import grab.bit.shared.ui.widget.table.customtable.TableCell
import androidx.compose.ui.unit.dp
import grab.bit.resources.Res
import grab.bit.shared.pages.credits.translators.LanguageTranslationInfo
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource

sealed interface TranslatorsCells : TableCell<LanguageTranslationInfo> {
    data object LanguageName : TranslatorsCells,
        SortableCell<LanguageTranslationInfo> {
        override fun comparator(): Comparator<LanguageTranslationInfo> = compareBy { it.locale }
        override val id: String = "language"
        override val name: StringSource = Res.string.language.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 200.dp)
    }

    data object Translators : TranslatorsCells {
        override val id: String = "translators"
        override val name: StringSource = Res.string.translators.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 350.dp)
    }

    companion object {
        fun all() = listOf(
            LanguageName,
            Translators,
        )
    }
}
