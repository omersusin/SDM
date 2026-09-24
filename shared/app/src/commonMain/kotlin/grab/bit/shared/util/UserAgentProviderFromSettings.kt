package grab.bit.shared.util

import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.downloader.connection.UserAgentProvider

class UserAgentProviderFromSettings(
    private val appSettingsStorage: BaseAppSettingsStorage
) : UserAgentProvider {
    override fun getUserAgent(): String? {
        return appSettingsStorage.userAgent.value.takeIf { it.isNotBlank() }
    }
}
