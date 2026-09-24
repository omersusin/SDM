package grab.bit.shared.util.dns

import grab.bit.resources.Res
import grab.bit.shared.storage.DnsSettings
import grab.bit.shared.util.dns.DnsModes.*
import grab.bit.util.HttpUrlUtils
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource

// switch for UI
enum class DnsModes(
    val stringSource: StringSource
) {
    System(Res.string.settings_dns_system.asStringSource()),
    DnsOverHttps(Res.string.settings_dns_doh.asStringSource()),
}

sealed interface DNSOption {
    data object System : DNSOption
    data class DnsOverHttps(
        val url: String,
    ) : DNSOption {
        companion object {
            fun isValid(url: String): Boolean {
                val isHttps = url.startsWith("https://")
                return isHttps && HttpUrlUtils.isValidUrl(url)
            }
        }
    }

    companion object {
        fun parse(
            string: String,
        ): DNSOption? {
            if (DnsOverHttps.isValid(string)) {
                return DnsOverHttps(string)
            }
            return null
        }
    }
}

fun DnsSettings.toDnsOptionOrDefault(): DNSOption {
    return when (mode) {
        System -> DNSOption.System
        DnsOverHttps -> DNSOption.parse(address)
    } ?: DNSOption.System
}

fun DNSOption.toEnum(): DnsModes {
    return when (this) {
        is DNSOption.DnsOverHttps -> DnsModes.DnsOverHttps
        DNSOption.System -> DnsModes.System
    }
}
