package grab.bit.desktop.utils

import grab.bit.desktop.AppArguments
import grab.bit.shared.util.schemakt.initializeForABDM
import io.github.amir1376.schemakt.Schema
import grab.bit.util.guardedEntry
import grab.bit.util.logger.AppLogger

enum class EntryType {
    GUI,
    CLI,
    NativeMessaging,
}

object EntrypointInitializer {
    private val booted = guardedEntry()
    fun boot(
        debug: Boolean = false,
        entryType: EntryType
    ) {
        booted.action {
            AppArguments.update {
                it.copy(
                    debug = debug,
                )
            }
            AppProperties.boot(AppInfo.definedPaths.appPropertiesFile)
            AppLogger.init(
                writeToConsole = false,
                logFilePath = AppInfo.definedPaths.logDir
                    .let {
                        it / when (entryType) {
                            EntryType.GUI -> "AppLog.log"
                            EntryType.CLI -> "CliLog.log"
                            EntryType.NativeMessaging -> "NativeMessaging.log"
                        }
                    }.takeIf {
                        AppInfo.isInDebugMode()
                    },
            )
            Schema.initializeForABDM()
        }
    }
}
