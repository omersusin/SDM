package grab.bit.android.pages.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import grab.bit.android.ui.page.FooterFade
import grab.bit.android.ui.page.PageUi
import grab.bit.android.ui.page.PageHeader
import grab.bit.android.ui.page.PageTitle
import grab.bit.android.ui.page.createAlphaForHeader
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.RenderConfigurableGroup
import grab.bit.shared.ui.widget.TransparentIconActionButton
import grab.bit.shared.util.ui.VerticalScrollableContent
import grab.bit.shared.util.ui.icon.MyIcons
import grab.bit.shared.util.ui.myColors
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource


@Composable
fun SettingsPage(
    settingsComponent: AndroidSettingsComponent,
) {
    val scrollState = rememberScrollState()
    var pageContentPaddingValues by remember {
        mutableStateOf(PaddingValues())
    }
    val topPadding = pageContentPaddingValues.calculateTopPadding()
    val bottomPadding = pageContentPaddingValues.calculateBottomPadding()
    val density = LocalDensity.current
    PageUi(
        header = {
            val backDispatcher = LocalOnBackPressedDispatcherOwner.current
            PageHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        myColors.background.copy(
                            createAlphaForHeader(
                                scrollState.value.toFloat(),
                                density.run { topPadding.toPx() },
                            ) * 0.75f
                        )
                    )
                    .statusBarsPadding(),
                leadingIcon = {
                    TransparentIconActionButton(
                        icon = MyIcons.back,
                        contentDescription = Res.string.back.asStringSource(),
                        onClick = {
                            backDispatcher?.onBackPressedDispatcher?.onBackPressed()
                        }
                    )
                },
                headerTitle = {
                    PageTitle(myStringResource(Res.string.settings))
                },
            )
        },
        footer = {
            Spacer(Modifier.navigationBarsPadding())
        }
    ) { params ->
        pageContentPaddingValues = params.paddingValues
        Box {
            VerticalScrollableContent(
                scrollState,
                Modifier.fillMaxSize()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .navigationBarsPadding()
                        .padding(bottom = 8.dp)
                        .padding(
                            horizontal = 8.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(topPadding))
                    val configurableGroups by settingsComponent.configurables.collectAsState()
                    for (cfgGroup in configurableGroups) {
                        RenderConfigurableGroup(
                            cfgGroup,
                            Modifier,
                            itemPadding = PaddingValues(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                        )
                    }
                }
            }
            FooterFade(bottomPadding)
        }
    }
}

