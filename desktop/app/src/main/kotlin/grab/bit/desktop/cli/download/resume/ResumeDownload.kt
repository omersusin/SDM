package grab.bit.desktop.cli.download.resume

import grab.bit.desktop.utils.singleInstance.SingleInstanceManager
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.long

class ResumeDownload : SuspendingCliktCommand("resume") {
    override fun help(context: Context) = "Resume download(s)"
    val id by argument("id").long().multiple()
    override suspend fun run() {
        SingleInstanceManager.get().awokenAppIPCService().getService().useService {
            it.resumeDownload(id)
        }
    }
}
