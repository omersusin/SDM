package grab.bit.android.pages.onboarding.initialsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import grab.bit.android.pages.onboarding.AppIcon
import grab.bit.android.pages.onboarding.StartUpPageActions
import grab.bit.android.pages.onboarding.StartUpPageHeader
import grab.bit.android.pages.onboarding.StartUpPageTemplate
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.ui.configurable.RenderConfigurable
import grab.bit.shared.ui.widget.PrimaryMainActionButton
import grab.bit.shared.ui.widget.Text
import grab.bit.shared.util.ui.LocalContentAlpha
import grab.bit.shared.util.ui.LocalContentColor
import grab.bit.shared.util.ui.myColors
import grab.bit.shared.util.ui.theme.myShapes
import grab.bit.shared.util.ui.theme.mySpacings
import grab.bit.shared.util.ui.theme.myTextSizes
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.resources.myStringResource

@Composable
fun InitialSetupPage(
    component: InitialSetupComponent,
) {
    StartUpPageTemplate(
        header = {
            StartUpPageHeader(
                title = Res.string.app_title.asStringSource(),
                onBackPressed = null
            )
        },
        actions = {
            StartUpPageActions {
                Column {
                    Text(
                        text = myStringResource(Res.string.initial_setup_notice),
                        color = LocalContentColor.current.copy(alpha = 0.75f),
                        modifier = Modifier
                            .padding(mySpacings.smallSpace)
                    )
                    Spacer(modifier = Modifier.height(mySpacings.mediumSpace))
                    Row {
                        PrimaryMainActionButton(
                            onClick = component::onUserPressFinish,
                            text = myStringResource(Res.string.next),
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        },
        content = {
            Column {
                Column(
                    Modifier
                        .weight(1f)
                        .wrapContentHeight()
                ) {
                    AppIcon(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(),
                        size = 72.dp,
                    )
                    Spacer(Modifier.height(mySpacings.largeSpace))
                    Spacer(Modifier.height(mySpacings.largeSpace))
                    Text(
                        text = myStringResource(Res.string.welcome),
                        fontWeight = FontWeight.Bold,
                        fontSize = myTextSizes.x2l,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(),
                    )
                    Spacer(Modifier.height(mySpacings.mediumSpace))
                    Text(
                        text = myStringResource(Res.string.initial_setup_description),
                        fontSize = myTextSizes.lg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(),
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .wrapContentHeight(Alignment.Bottom)
                        .padding(vertical = mySpacings.largeSpace),
                    verticalArrangement = Arrangement.spacedBy(mySpacings.largeSpace),
                ) {
                    for (configurable in component.configurables) {
                        RenderConfigurable(
                            cfg = configurable,
                            configurableUiProps = ConfigurableUiProps(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = mySpacings.largeSpace)
                                    .clip(myShapes.defaultRounded)
                                    .background(myColors.surface),
                                itemPaddingValues = PaddingValues(
                                    horizontal = mySpacings.largeSpace,
                                    vertical = mySpacings.mediumSpace,
                                ),
                            )
                        )
                    }
                }
            }
        }
    )
}
