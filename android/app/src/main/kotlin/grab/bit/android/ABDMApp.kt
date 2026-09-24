package grab.bit.android

import android.app.Application
import co.touchlab.kermit.Severity
import grab.bit.android.di.Di
import grab.bit.android.util.ABDMAppManager
import grab.bit.android.util.AndroidGlobalExceptionHandler
import grab.bit.android.util.AppInfo
import grab.bit.android.util.ApplicationBackgroundTracker
import grab.bit.shared.repository.BaseAppRepository
import grab.bit.shared.util.appinfo.PreviousVersion
import grab.bit.shared.util.schemakt.initializeForABDM
import io.github.amir1376.schemakt.Schema
import grab.bit.downloader.video.YtDlpRunner
import grab.bit.util.logger.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ABDMApp : Application(), KoinComponent {
    val appManager: ABDMAppManager by inject()
    val appRepository: BaseAppRepository by inject()
    val previousVersion: PreviousVersion by inject()
    val scope: CoroutineScope by inject()
    override fun onCreate() {
        super.onCreate()
        AppInfo.init(this)
        AppLogger.init(
            writeToConsole = true,
            logFilePath = null,
            minSeverity = Severity.Verbose,
        )
        Di.boot(this)
        Schema.initializeForABDM()
        ApplicationBackgroundTracker.startTracking(this)
        appRepository.boot()
        previousVersion.boot()
        Thread.setDefaultUncaughtExceptionHandler(
            AndroidGlobalExceptionHandler(
                this,
                Thread.getDefaultUncaughtExceptionHandler(),
            )
        )
        appManager.boot()
        scope.launch(Dispatchers.IO) {
            runCatching {
                YtDlpRunner.init(this@ABDMApp)
            }
        }
    }
}
