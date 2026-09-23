package com.abdownloadmanager.android.pages.settings

import com.abdownloadmanager.android.pages.onboarding.permissions.ABDMPermissions
import com.abdownloadmanager.android.storage.AppSettingsStorage
import com.abdownloadmanager.android.ui.configurable.android.item.PermissionConfigurable
import com.abdownloadmanager.android.util.pagemanager.PermissionsPageManager
import com.abdownloadmanager.resources.Res
import com.abdownloadmanager.shared.ui.configurable.item.BooleanConfigurable
import com.abdownloadmanager.shared.ui.configurable.item.EnumConfigurable
import com.abdownloadmanager.shared.ui.configurable.item.NavigatableConfigurable
import ir.amirab.util.GrabberUiMode
import ir.amirab.util.compose.asStringSource
import kotlinx.coroutines.flow.MutableStateFlow


object AndroidSettings {
    fun permissionSettings(
        permissionsPageManager: PermissionsPageManager
    ): NavigatableConfigurable {
        return NavigatableConfigurable(
            title = Res.string.permissions.asStringSource(),
            description = "".asStringSource(),
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
}
