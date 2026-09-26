package grab.bit.android.storage

import androidx.datastore.core.DataStore
import grab.bit.android.pages.home.HomePageStateToPersist
import grab.bit.android.pages.home.sortBy
import grab.bit.shared.util.ConfigBaseSettingsByJson

class HomePageStorage(
    dataStore: DataStore<HomePageStateToPersist>,
) : ConfigBaseSettingsByJson<HomePageStateToPersist>(
    dataStore = dataStore,
) {
    val sortBy = from(HomePageStateToPersist.sortBy)
    val manualOrder = from(HomePageStateToPersist.manualOrder)
}
