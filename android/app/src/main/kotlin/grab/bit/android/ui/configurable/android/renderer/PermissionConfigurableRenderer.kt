package grab.bit.android.ui.configurable.android.renderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import grab.bit.android.pages.onboarding.permissions.AppPermissionState
import grab.bit.android.pages.onboarding.permissions.rememberAppPermissionState
import grab.bit.android.ui.configurable.ConfigTemplate
import grab.bit.android.ui.configurable.NextIcon
import grab.bit.android.ui.configurable.TitleAndDescription
import grab.bit.android.ui.configurable.android.item.PermissionConfigurable
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.ConfigurableRenderer
import grab.bit.shared.ui.configurable.ConfigurableUiProps
import grab.bit.shared.util.ui.LocalContentColor
import grab.bit.shared.util.ui.myColors
import grab.bit.util.compose.asStringSource

object PermissionConfigurableRenderer : ConfigurableRenderer<PermissionConfigurable> {
    @Composable
    override fun RenderConfigurable(
        configurable: PermissionConfigurable,
        configurableUiProps: ConfigurableUiProps
    ) {
        val permission by configurable.stateFlow.collectAsState()
        val permissionState = rememberAppPermissionState(permission) { result ->

        }

        RenderPermissionConfigurable(
            cfg = configurable,
            configurableUiProps = configurableUiProps,
            permissionState = permissionState
        )
    }

    @Composable
    fun RenderPermissionConfigurable(
        cfg: PermissionConfigurable,
        configurableUiProps: ConfigurableUiProps,
        permissionState: AppPermissionState,
    ) {
        ConfigTemplate(
            modifier = configurableUiProps.modifier
                .clickable {
                    permissionState.launchRequest()
                }
                .padding(configurableUiProps.itemPaddingValues),
            title = {
                TitleAndDescription(
                    cfg = cfg,
                    describe = true,
                    describeContent = if (permissionState.isGranted) {
                        Res.string.permission_granted
                    } else {
                        Res.string.permission_not_granted
                    }.asStringSource().rememberString(),
                    describeWrapper = { content ->
                        val contentColor =
                            if (permissionState.isGranted) myColors.success else myColors.warning
                        CompositionLocalProvider(
                            LocalContentColor provides contentColor
                        ) {
                            content()
                        }
                    }
                )
            },
            value = {
                NextIcon()
            }
        )
    }
}
