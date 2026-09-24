package grab.bit.desktop.ui.configurable.comon

import grab.bit.desktop.ui.configurable.comon.renderer.BooleanConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.DayOfWeekConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.DnsConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.EnumConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.FileChecksumConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.FloatConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.FolderConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.IntConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.LongConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.PerHostSettingsConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.SpeedLimitConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.StringConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.ThemeConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.TimeConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.ProxyConfigurableRenderer
import grab.bit.desktop.ui.configurable.comon.renderer.SoundConfigurableRenderer
import grab.bit.shared.ui.configurable.CommonConfigurableRenderers

val CommonConfigurableRenderersForDesktop = CommonConfigurableRenderers(
    booleanConfigurableRenderer = BooleanConfigurableRenderer,
    dayOfWeekConfigurableRenderer = DayOfWeekConfigurableRenderer,
    fileChecksumConfigurableRenderer = FileChecksumConfigurableRenderer,
    floatConfigurableRenderer = FloatConfigurableRenderer,
    folderConfigurableRenderer = FolderConfigurableRenderer,
    intConfigurableRenderer = IntConfigurableRenderer,
    longConfigurableRenderer = LongConfigurableRenderer,
    perHostSettingsConfigurableRenderer = PerHostSettingsConfigurableRenderer,
    enumConfigurableRenderer = EnumConfigurableRenderer,
    speedConfigurableRenderer = SpeedLimitConfigurableRenderer,
    stringConfigurableRenderer = StringConfigurableRenderer,
    themeConfigurableRenderer = ThemeConfigurableRenderer,
    timeConfigurableRenderer = TimeConfigurableRenderer,
    proxyConfigurableRenderer = ProxyConfigurableRenderer,
    dnsConfigurableRenderer = DnsConfigurableRenderer,
    soundConfigurableRenderer = SoundConfigurableRenderer,
)
