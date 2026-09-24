package grab.bit.shared.util

import grab.bit.shared.BuildConfig
import io.github.z4kn4fein.semver.Version

object AppVersion {
    private val currentVersion = Version.parse(BuildConfig.APP_VERSION)
    fun get() = currentVersion
}
