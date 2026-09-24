package grab.bit.shared.storage

import androidx.datastore.core.DataStore
import grab.bit.shared.util.ConfigBaseSettingsByJson
import grab.bit.shared.util.proxy.IProxyStorage
import grab.bit.shared.util.proxy.ProxyData

class ProxyDatastoreStorage(
    dataStore: DataStore<ProxyData>,
) : IProxyStorage, ConfigBaseSettingsByJson<ProxyData>(dataStore) {
    override val proxyDataFlow = data
}
