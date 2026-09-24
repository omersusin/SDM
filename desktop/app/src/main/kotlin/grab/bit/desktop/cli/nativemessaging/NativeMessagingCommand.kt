package grab.bit.desktop.cli.nativemessaging

import grab.bit.desktop.AppArguments
import grab.bit.desktop.cli.nativemessaging.install.NativeMessagingInstallCommand
import grab.bit.desktop.cli.nativemessaging.run.NativeMessagingRunRunCommand
import grab.bit.desktop.cli.nativemessaging.uninstall.NativeMessagingUninstallCommand
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands

class NativeMessagingCommand : SuspendingCliktCommand(
    "native-messaging"
) {

    init {
        subcommands(
            NativeMessagingInstallCommand(),
            NativeMessagingUninstallCommand(),
            NativeMessagingRunRunCommand(),
        )
    }

    override suspend fun run() = Unit
}

