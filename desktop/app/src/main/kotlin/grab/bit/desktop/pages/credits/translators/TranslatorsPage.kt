package grab.bit.desktop.pages.credits.translators

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import grab.bit.desktop.di.Di
import grab.bit.shared.ui.widget.MaybeLinkText
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.ui.widget.table.customtable.Table
import grab.bit.shared.ui.widget.table.customtable.TableState
import grab.bit.shared.ui.widget.table.customtable.styled.MyStyledTableHeader
import grab.bit.desktop.utils.AppInfo
import grab.bit.shared.util.div
import grab.bit.resources.Res
import grab.bit.shared.pages.credits.translators.LanguageTranslationInfo
import grab.bit.shared.pages.credits.translators.TranslatorData
import grab.bit.shared.ui.widget.PrimaryMainActionButton
import grab.bit.shared.util.ui.LocalContentColor
import grab.bit.shared.util.ui.WithContentAlpha
import grab.bit.util.URLOpener
import grab.bit.util.compose.localizationmanager.LanguageNameProvider
import grab.bit.util.compose.localizationmanager.MyLocale
import grab.bit.util.compose.resources.myStringResource
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.component.get

@Composable
internal fun Translators(modifier: Modifier) {
    Column(
        modifier
    ) {
        TranslatorsTable(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        ContributionNotice(
            modifier = Modifier,
            onUserWantsToContribute = {
                URLOpener.openUrl(AppInfo.translationsUrl)
            }
        )
    }
}

@Composable
private fun ContributionNotice(
    modifier: Modifier,
    onUserWantsToContribute: () -> Unit,
) {
    Column(modifier) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(myColors.onBackground / 0.15f)
        )
        Column(
            Modifier
                .fillMaxWidth()
                .background(myColors.surface / 0.5f)
                .padding(horizontal = 32.dp)
                .padding(vertical = 16.dp),
        ) {
            Text(
                myStringResource(Res.string.translators_page_thanks),
                modifier = Modifier,
                fontSize = myTextSizes.lg,
                fontWeight = FontWeight.Bold,
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(1.dp)
                    .background(myColors.surface)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Text(
                        myStringResource(Res.string.translators_contribute_title),
                        fontSize = myTextSizes.lg,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        myStringResource(Res.string.translators_contribute_description),
                        fontSize = myTextSizes.base,
                        color = LocalContentColor.current / 0.75f
                    )
                }
                Spacer(Modifier.width(32.dp))
                PrimaryMainActionButton(
                    text = myStringResource(Res.string.contribute),
                    onClick = onUserWantsToContribute,
                    modifier = Modifier,
                    enabled = true,
                )
            }
        }
    }
}


@Composable
private fun TranslatorsTable(
    modifier: Modifier,
) {
    val tableState = remember {
        TableState(
            cells = TranslatorsCells.all()
        )
    }
    val itemHorizontalPadding = 16.dp
    Table(
        modifier = modifier,
        list = rememberLanguageTranslationInfo(),
        listState = rememberLazyListState(),
        tableState = tableState,
        wrapHeader = {
            MyStyledTableHeader(
                itemHorizontalPadding = itemHorizontalPadding,
                content = it,
            )
        },
        wrapItem = { index, _, rowContent ->
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .widthIn(getTableSize().visibleWidth)
                    .hoverable(interactionSource)
                    .indication(
                        interactionSource,
                        LocalIndication.current,
                    )
                    .background(
                        if (index % 2 == 0) Color.Transparent else myColors.surface / 0.35f
                    )
                    .padding(vertical = 12.dp, horizontal = itemHorizontalPadding)
            ) {
                rowContent()
            }
        },
        renderCell = { libraryCell, translationInfo ->
            when (libraryCell) {
                TranslatorsCells.LanguageName -> {
                    Column {
                        WithContentAlpha(1f) {
                            Text(
                                translationInfo.nativeName,
                                fontSize = myTextSizes.base,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        WithContentAlpha(0.75f) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    translationInfo.englishName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = myTextSizes.base,
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    translationInfo.locale,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = myTextSizes.base,
                                    color = myColors.primary,
                                    modifier = Modifier
                                        .background(myColors.primary / 10)
                                        .padding(vertical = 0.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                TranslatorsCells.Translators -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        translationInfo.translators.forEach {
                            MaybeLinkText(
                                it.name,
                                it.link,
                            )
                        }
                    }
                }
            }
        },
    )
}

private fun convertLanguageToMyLocale(language: String): MyLocale {
    return language.split("-").run {
        MyLocale(
            languageCode = get(0),
            countryCode = getOrNull(1)
        )
    }
}

@Composable
private fun rememberLanguageTranslationInfo(): List<LanguageTranslationInfo> {
    return remember {
        val json = Di.get<Json>()
        val translatorData = FileSystem.RESOURCES.read(
            "/grab/bit/resources/credits/translators.json".toPath(),
            {
                readUtf8()
            }
        ).let {
            json.decodeFromString<TranslatorData>(it)
        }
        translatorData.map {
            val name = LanguageNameProvider.getName(convertLanguageToMyLocale(it.key))
            LanguageTranslationInfo(
                locale = it.key,
                englishName = name.englishName,
                nativeName = name.nativeName,
                translators = it.value,
            )
        }
    }
}
