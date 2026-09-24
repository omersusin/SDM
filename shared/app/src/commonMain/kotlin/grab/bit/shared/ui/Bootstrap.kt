package grab.bit.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.ui.configurable.ConfigurableRendererRegistry
import grab.bit.shared.ui.configurable.LocalConfigurationRendererRegistry
import grab.bit.shared.util.LocalUseRelativeDateTime
import grab.bit.shared.util.ProvideSizeAndSpeedUnit
import grab.bit.util.compose.IIconResolver
import grab.bit.util.compose.LocalIconFromUriResolver


@Composable
fun ProvideCommonSettings(
    appSettings: BaseAppSettingsStorage,
    iconProvider: IIconResolver,
    configurableRendererRegistry: ConfigurableRendererRegistry,
    content: @Composable () -> Unit,
) {
    val useNativeDateTime by appSettings.useRelativeDateTime.collectAsState()
    CompositionLocalProvider(
        LocalUseRelativeDateTime provides useNativeDateTime,
        LocalIconFromUriResolver provides iconProvider,
        LocalConfigurationRendererRegistry provides configurableRendererRegistry,
    ) {
        content()
    }
}

@Composable
fun ProvideSizeUnits(
    appRepository: BaseAppRepository,
    content: @Composable () -> Unit,
) {
    ProvideSizeAndSpeedUnit(
        sizeUnitConfig = appRepository.sizeUnit.collectAsState().value,
        speedUnitConfig = appRepository.speedUnit.collectAsState().value,
        content = content
    )
}
