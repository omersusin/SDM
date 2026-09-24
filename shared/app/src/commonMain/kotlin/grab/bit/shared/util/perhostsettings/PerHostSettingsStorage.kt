package grab.bit.shared.util.perhostsettings

import kotlinx.coroutines.flow.MutableStateFlow

interface IPerHostSettingsStorage {
    val perHostSettingsFlow: MutableStateFlow<List<PerHostSettingsItem>>
}
