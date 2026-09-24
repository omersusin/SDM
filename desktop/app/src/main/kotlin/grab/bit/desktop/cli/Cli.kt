package grab.bit.desktop.cli

import grab.bit.desktop.AppArguments
import grab.bit.desktop.cli.download.Download
import grab.bit.desktop.cli.gui.Gui
import grab.bit.desktop.cli.nativemessaging.NativeMessagingCommand
import grab.bit.desktop.utils.AppInfo
import grab.bit.desktop.utils.AppProperties
import grab.bit.desktop.utils.EntryType
import grab.bit.desktop.utils.EntrypointInitializer
import grab.bit.desktop.utils.isInDebugMode
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import grab.bit.util.logger.AppLogger

class Cli : SuspendingCliktCommand("ABDownloadManagerCli") {

    val debug by option(AppArguments.Args.DEBUG).flag()

    init {
        versionOption(AppInfo.version.toString())
        subcommands(
            Gui(),
            Download(),
            NativeMessagingCommand(),
        )
    }

    override suspend fun run() {
        EntrypointInitializer.boot(
            debug = debug,
            entryType = EntryType.CLI,
        )
    }

}
