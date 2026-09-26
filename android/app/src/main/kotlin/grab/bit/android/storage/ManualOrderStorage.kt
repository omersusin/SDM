package grab.bit.android.storage

import androidx.datastore.core.DataStore
import grab.bit.shared.util.ConfigBaseSettingsByJson
import kotlinx.coroutines.flow.MutableStateFlow

class ManualOrderStorage(
    dataStore: DataStore<List<Long>>,
) : ConfigBaseSettingsByJson<List<Long>>(dataStore) {
    val manualOrder: MutableStateFlow<List<Long>> = data
}
