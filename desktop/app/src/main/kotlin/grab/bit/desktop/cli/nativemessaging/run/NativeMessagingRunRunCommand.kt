package grab.bit.desktop.cli.nativemessaging.run

import grab.bit.desktop.AppArguments
import grab.bit.desktop.nativemessaging.host.NativeMessagingHostLauncher
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context

class NativeMessagingRunRunCommand : SuspendingCliktCommand(
    "run"
) {

    override fun help(context: Context): String =
        "Native Messaging Host: Runs the native messaging host directly. This is intended for testing purposes only. Do not use it."

    override suspend fun run() {
        NativeMessagingHostLauncher().main(emptyArray())
    }
}
