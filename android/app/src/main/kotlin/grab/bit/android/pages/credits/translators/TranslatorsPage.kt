package grab.bit.android.pages.credits.translators

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import grab.bit.android.di.Di
import grab.bit.resources.ABDMResources
import grab.bit.shared.ui.widget.MaybeLinkText
import kotlinx.coroutines.runBlocking

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.page.FooterFade
import grab.bit.android.ui.page.PageHeader
import grab.bit.android.ui.page.PageTitle
import grab.bit.android.ui.page.PageUi
import grab.bit.android.ui.page.rememberHeaderAlpha
import grab.bit.android.util.compose.useBack
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.div
import grab.bit.resources.Res
import grab.bit.shared.pages.credits.translators.LanguageTranslationInfo
import grab.bit.shared.pages.credits.translators.TranslatorData
import grab.bit.shared.ui.widget.PrimaryMainActionButton
import grab.bit.shared.ui.widget.TransparentIconActionButton
import grab.bit.shared.util.SharedConstants
import grab.bit.shared.util.ui.LocalContentColor
import grab.bit.shared.util.ui.WithContentAlpha
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.util.URLOpener
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.dpToPx
import grab.bit.util.compose.localizationmanager.LanguageNameProvider
import grab.bit.util.compose.localizationmanager.MyLocale
import grab.bit.util.compose.resources.myStringResource
import grab.bit.util.ifThen
import kotlinx.serialization.json.Json
import org.koin.core.component.get

@Composable
fun TranslatorsPage(onBack: () -> Unit) {
    Translators(
        Modifier
            .fillMaxSize()
            .background(myColors.background)
    )
}

@Composable
internal fun Translators(modifier: Modifier) {
    val listState = rememberLazyListState()
    var contentPadding by remember {
        mutableStateOf(PaddingValues.Zero)
    }
    val topPadding = contentPadding.calculateTopPadding()
    val bottomPadding = contentPadding.calculateBottomPadding()
    val density = LocalDensity.current
    val headerAlpha by rememberHeaderAlpha(listState, topPadding.dpToPx(density))
    PageUi(
        modifier = modifier,
        header = {
            val onBack = useBack()
            PageHeader(
                leadingIcon = {
                    TransparentIconActionButton(
                        MyIcons.back,
                        Res.string.back.asStringSource(),
                    ) {
                        onBack?.onBackPressed()
                    }
                },
                headerTitle = {
                    PageTitle(
                        myStringResource(Res.string.meet_the_translators)
                    )
                },
                modifier = Modifier
                    .background(
                        myColors.background.copy(
                            alpha = headerAlpha * 0.75f
                        )
                    )
                    .statusBarsPadding()
            )
        },
        footer = {
            AnimatedContent(
                headerAlpha == 0f,
                transitionSpec = {
                    fadeIn() + expandVertically() togetherWith fadeOut() + shrinkVertically()
                },
                contentAlignment = Alignment.BottomCenter,
            ) {
                if (it) {
                    ContributionNotice(
                        modifier = Modifier,
                        onUserWantsToContribute = {
                            URLOpener.openUrl(SharedConstants.projectTranslations)
                        }
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    )
                }
            }
        },
    ) {
        contentPadding = it.paddingValues
        Box {
            DearTranslators(
                Modifier
                    .fillMaxWidth(),
                state = listState,
                contentPadding = it.paddingValues,
            )
            FooterFade(bottomPadding)
        }
    }
}

@Composable
private fun ContributionNotice(
    modifier: Modifier,
    onUserWantsToContribute: () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(myColors.surface),
    ) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(myColors.onSurface / 0.15f)
        )
        Column(
            Modifier
                .padding(mySpacings.largeSpace)
                .navigationBarsPadding()
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
            }
            Spacer(Modifier.height(mySpacings.largeSpace))
            PrimaryMainActionButton(
                text = myStringResource(Res.string.contribute),
                onClick = onUserWantsToContribute,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            )
        }
    }
}

@Composable
private fun DearTranslators(
    modifier: Modifier,
    state: LazyListState,
    contentPadding: PaddingValues,
) {
    val itemHorizontalPadding = 16.dp
    val list = rememberLanguageTranslationInfo()

    LazyColumn(
        modifier,
        state = state,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(list, key = { _, item -> item.locale }) { index, item ->
            TranslatedLanguageItem(
                item,
                Modifier
                    .fillMaxWidth()
                    .ifThen(index % 2 == 1) {
                        background(myColors.surface)
                    }
                    .padding(16.dp, itemHorizontalPadding)
            )
        }
    }
}

@Composable
private fun TranslatedLanguageItem(
    translationInfo: LanguageTranslationInfo,
    modifier: Modifier,
) {
    Column(modifier) {
        Column {
            WithContentAlpha(1f) {
                Text(
                    translationInfo.nativeName,
                    fontSize = myTextSizes.base,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(mySpacings.smallSpace))
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
        Spacer(Modifier.height(mySpacings.mediumSpace))
        Column(
            verticalArrangement = Arrangement.spacedBy(mySpacings.smallSpace)
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
        val translatorData = runBlocking {
            ABDMResources.getTranslatorsContent()
        }.let {
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
