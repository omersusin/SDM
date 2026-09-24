package grab.bit.shared.storage.impl

import androidx.datastore.core.DataStore
import grab.bit.shared.storage.ILastSavedLocationsStorage
import grab.bit.shared.storage.ISelectQueueStorage
import grab.bit.shared.storage.SelectQueueSettings
import grab.bit.shared.util.ConfigBaseSettingsByJson
import kotlinx.coroutines.flow.MutableStateFlow

class SelectQueueStorage(
    dataStore: DataStore<SelectQueueSettings>
) : ConfigBaseSettingsByJson<SelectQueueSettings>(dataStore), ISelectQueueStorage {
    override val selectQueueSettings: MutableStateFlow<SelectQueueSettings> = data
}
