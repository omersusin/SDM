package grab.bit.android.pages.settings

import grab.bit.android.pages.onboarding.permissions.ABDMPermissions
import grab.bit.android.storage.AppSettingsStorage
import grab.bit.android.ui.configurable.android.item.PermissionConfigurable
import grab.bit.android.util.pagemanager.PermissionsPageManager
import grab.bit.resources.Res
import grab.bit.shared.ui.configurable.item.BooleanConfigurable
import grab.bit.shared.ui.configurable.item.EnumConfigurable
import grab.bit.shared.ui.configurable.item.NavigatableConfigurable
import grab.bit.shared.ui.configurable.item.StringConfigurable
import grab.bit.util.GrabberUiMode
import grab.bit.util.VideoQuality
import grab.bit.util.compose.asStringSource
import kotlinx.coroutines.flow.MutableStateFlow


object AndroidSettings {
    fun permissionSettings(
        permissionsPageManager: PermissionsPageManager
    ): NavigatableConfigurable {
        return NavigatableConfigurable(
            title = Res.string.permissions.asStringSource(),
            description = Res.string.settings_permissions_description.asStringSource(),
            onRequestNavigate = {
                permissionsPageManager.openPermissionsPage(false)
            },
        )
    }

    fun ignoreBatteryOptimizations(): PermissionConfigurable {
        val permission = ABDMPermissions.BatteryOptimizationPermission
        return PermissionConfigurable(
            title = permission.title,
            description = permission.description,
            backedBy = MutableStateFlow(permission),
        )
    }

    fun browserIconInLauncher(
        appSettingsStorage: AppSettingsStorage
    ): BooleanConfigurable {
        return BooleanConfigurable(
            title = Res.string.settings_browser_in_launcher.asStringSource(),
            description = Res.string.settings_browser_in_launcher_description.asStringSource(),
            backedBy = appSettingsStorage.browserIconInLauncher,
            describe = {
                if (it) {
                    Res.string.enabled
                } else {
                    Res.string.disabled
                }.asStringSource()
            }
        )
    }

    fun grabberUiMode(
        appSettingsStorage: AppSettingsStorage
    ): EnumConfigurable<GrabberUiMode> {
        return EnumConfigurable(
            title = Res.string.settings_grabber_ui_mode.asStringSource(),
            description = Res.string.settings_grabber_ui_mode_description.asStringSource(),
            backedBy = appSettingsStorage.grabberUiMode,
            possibleValues = GrabberUiMode.entries.toList(),
            describe = {
                when (it) {
                    GrabberUiMode.MENU_BADGE -> Res.string.settings_grabber_ui_mode_menu_badge
                    GrabberUiMode.AUTO_POPUP -> Res.string.settings_grabber_ui_mode_auto_popup
                }.asStringSource()
            }
        )
    }

    fun adBlockEnabled(
        appSettingsStorage: AppSettingsStorage
    ): BooleanConfigurable {
        return BooleanConfigurable(
            title = Res.string.settings_adblock.asStringSource(),
            description = Res.string.settings_adblock_description.asStringSource(),
            backedBy = appSettingsStorage.adBlockEnabled,
            describe = {
                if (it) {
                    Res.string.enabled
                } else {
                    Res.string.disabled
                }.asStringSource()
            }
        )
    }

    fun wifiOnlyDownloads(
        appSettingsStorage: AppSettingsStorage
    ): BooleanConfigurable {
        return BooleanConfigurable(
            title = Res.string.settings_wifi_only.asStringSource(),
            description = Res.string.settings_wifi_only_description.asStringSource(),
            backedBy = appSettingsStorage.wifiOnlyDownloads,
            describe = {
                if (it) {
                    Res.string.enabled
                } else {
                    Res.string.disabled
                }.asStringSource()
            }
        )
    }

    fun ssidProfiles(
        appSettingsStorage: AppSettingsStorage
    ): StringConfigurable {
        return StringConfigurable(
            title = Res.string.settings_ssid_profiles.asStringSource(),
            description = Res.string.settings_ssid_profiles_description.asStringSource(),
            backedBy = appSettingsStorage.ssidProfiles,
            placeholder = Res.string.settings_ssid_profiles_placeholder.asStringSource(),
            describe = {
                if (it.isBlank()) {
                    Res.string.disabled.asStringSource()
                } else {
                    it.take(80).asStringSource()
                }
            },
        )
    }

    fun bindInterface(
        appSettingsStorage: AppSettingsStorage
    ): StringConfigurable {
        return StringConfigurable(
            title = Res.string.settings_bind_interface.asStringSource(),
            description = Res.string.settings_bind_interface_description.asStringSource(),
            backedBy = appSettingsStorage.bindInterface,
            placeholder = Res.string.settings_bind_interface_placeholder.asStringSource(),
            describe = {
                if (it.isBlank()) {
                    Res.string.disabled.asStringSource()
                } else {
                    it.take(80).asStringSource()
                }
            },
        )
    }

    fun videoQuality(
        appSettingsStorage: AppSettingsStorage
    ): EnumConfigurable<VideoQuality> {
        return EnumConfigurable(
            title = Res.string.settings_video_quality.asStringSource(),
            description = Res.string.settings_video_quality_description.asStringSource(),
            backedBy = appSettingsStorage.videoQuality,
            possibleValues = VideoQuality.entries.toList(),
            describe = {
                when (it) {
                    VideoQuality.AUTO -> Res.string.settings_video_quality_auto
                    VideoQuality.HIGHEST -> Res.string.settings_video_quality_highest
                    VideoQuality.P1080 -> Res.string.settings_video_quality_1080
                    VideoQuality.P720 -> Res.string.settings_video_quality_720
                    VideoQuality.P480 -> Res.string.settings_video_quality_480
                    VideoQuality.P360 -> Res.string.settings_video_quality_360
                }.asStringSource()
            }
        )
    }
}
