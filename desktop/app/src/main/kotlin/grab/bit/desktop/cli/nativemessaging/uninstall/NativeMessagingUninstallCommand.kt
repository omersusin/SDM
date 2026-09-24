package grab.bit.desktop.cli.nativemessaging.uninstall

import grab.bit.desktop.AppArguments
import grab.bit.desktop.nativemessaging.NativeMessaging
import grab.bit.desktop.nativemessaging.host.NativeMessagingHostLauncher
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import kotlinx.serialization.json.Json

class NativeMessagingUninstallCommand : SuspendingCliktCommand(
    "uninstall"
) {

    override fun help(context: Context): String = "Uninstalls the native messaging host manifest file"

    override suspend fun run() {
        NativeMessaging.getDefault(Json).uninstallManifests()
    }
}
