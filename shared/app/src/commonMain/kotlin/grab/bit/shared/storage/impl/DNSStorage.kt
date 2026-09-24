package grab.bit.shared.storage.impl

import androidx.datastore.core.DataStore
import grab.bit.shared.storage.DnsSettings
import grab.bit.shared.storage.IDNSSettingsStorage
import grab.bit.shared.util.ConfigBaseSettingsByJson
import grab.bit.shared.util.dns.DNSOption
import grab.bit.shared.util.dns.DnsOptionProvider
import grab.bit.shared.util.dns.toDnsOptionOrDefault
import grab.bit.util.singleEntryCache
import kotlinx.coroutines.flow.MutableStateFlow

class DNSStorage(
    dataStore: DataStore<DnsSettings>,
) : IDNSSettingsStorage,
    ConfigBaseSettingsByJson<DnsSettings>(dataStore),
    DnsOptionProvider {
    override val dnsSettingsFlow: MutableStateFlow<DnsSettings> = data

    val lastCachedValue = singleEntryCache<DnsSettings, DNSOption>()
    override fun getDNSOption(): DNSOption {
        return lastCachedValue.getOrCreate(dnsSettingsFlow.value) {
            it.toDnsOptionOrDefault()
        }
    }
}
