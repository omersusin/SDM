package grab.bit.shared.storage

import androidx.datastore.core.DataStore
import grab.bit.shared.util.ConfigBaseSettingsByJson
import grab.bit.shared.util.perhostsettings.IPerHostSettingsStorage
import grab.bit.shared.util.perhostsettings.PerHostSettingsItem
import kotlinx.coroutines.flow.MutableStateFlow

class PerHostSettingsDatastoreStorage(
    dataStore: DataStore<List<PerHostSettingsItem>>,
) : IPerHostSettingsStorage, ConfigBaseSettingsByJson<List<PerHostSettingsItem>>(dataStore) {
    override val perHostSettingsFlow: MutableStateFlow<List<PerHostSettingsItem>> = data
}
