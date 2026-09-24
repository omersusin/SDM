package grab.bit.desktop.cli.gui.integration

import grab.bit.desktop.utils.IntegrationPortBroadcaster
import grab.bit.desktop.utils.singleInstance.SingleInstanceManager
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context

class ShowIntegrationPort : SuspendingCliktCommand("show") {
    override fun help(context: Context) = "Show integration port and exit"
    override suspend fun run() {
        val singleInstance = SingleInstanceManager.get()

        val port = runCatching {
            singleInstance.singleInstanceService().useService { it.getIntegrationPort() }
        }.getOrElse { IntegrationPortBroadcaster.INTEGRATION_UNKNOWN }
        echo(port)
    }
}
