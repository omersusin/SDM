package grab.bit.android.ui.configurable.comon

import grab.bit.android.ui.configurable.comon.renderer.BooleanConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.DayOfWeekConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.DnsConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.EnumConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.FileChecksumConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.FloatConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.FolderConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.IntConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.LongConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.NavigatableConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.ProxyConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.SoundConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.SpeedLimitConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.StringConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.ThemeConfigurableRenderer
import grab.bit.android.ui.configurable.comon.renderer.TimeConfigurableRenderer
import grab.bit.shared.ui.configurable.CommonConfigurableRenderers

val CommonConfigurableRenderersForAndroid = CommonConfigurableRenderers(
    booleanConfigurableRenderer = BooleanConfigurableRenderer,
    dayOfWeekConfigurableRenderer = DayOfWeekConfigurableRenderer,
    fileChecksumConfigurableRenderer = FileChecksumConfigurableRenderer,
    floatConfigurableRenderer = FloatConfigurableRenderer,
    folderConfigurableRenderer = FolderConfigurableRenderer,
    intConfigurableRenderer = IntConfigurableRenderer,
    longConfigurableRenderer = LongConfigurableRenderer,
    perHostSettingsConfigurableRenderer = NavigatableConfigurableRenderer,
    enumConfigurableRenderer = EnumConfigurableRenderer,
    speedConfigurableRenderer = SpeedLimitConfigurableRenderer,
    stringConfigurableRenderer = StringConfigurableRenderer,
    themeConfigurableRenderer = ThemeConfigurableRenderer,
    timeConfigurableRenderer = TimeConfigurableRenderer,
    proxyConfigurableRenderer = ProxyConfigurableRenderer,
    dnsConfigurableRenderer = DnsConfigurableRenderer,
    soundConfigurableRenderer = SoundConfigurableRenderer,
)
