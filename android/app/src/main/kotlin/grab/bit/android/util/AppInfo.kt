package grab.bit.android.util

import android.app.Application
import grab.bit.BuildConfig
import grab.bit.shared.util.AppVersion
import grab.bit.shared.util.SharedConstants
import grab.bit.util.platform.Platform
import okio.Path.Companion.toOkioPath

object AppInfo {
    val isInDebugMode: Boolean = BuildConfig.DEBUG
    lateinit var context: Application
    fun init(context: Application) {
        this.context = context
    }

    val platform = Platform.Android
    val version = AppVersion.get()

    val definedPaths by lazy {
        AndroidDefinedPaths(
            dataDir = context.filesDir.resolve(
                SharedConstants.dataDirName
            ).toOkioPath()
        )
    }
}
