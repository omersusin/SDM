package grab.bit.shared.pagemanager

interface PerHostSettingsPageManager {
    fun openPerHostSettings(openedHost: String?)
    fun closePerHostSettings()
}
