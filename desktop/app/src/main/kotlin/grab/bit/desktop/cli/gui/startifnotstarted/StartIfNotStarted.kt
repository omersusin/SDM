package grab.bit.desktop.cli.gui.startifnotstarted

import grab.bit.desktop.AppArguments
import grab.bit.desktop.utils.AppInfo
import grab.bit.desktop.utils.isInIDE
import grab.bit.desktop.utils.singleInstance.StartIfNotStartedCommand
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.PrintMessage

class StartIfNotStarted : SuspendingCliktCommand(
    AppArguments.Commands.START_IF_NOT_STARTED
) {
    override suspend fun run() {
        if (AppInfo.isInIDE()) {
            throw PrintMessage(
                "we can't start the app, because the command executed from the IDE",
                statusCode = 1,
                printError = true
            )
        }
        val result = StartIfNotStartedCommand.startAndWaitForRunIfNotRunning()
        val isSuccessFull = result.isSuccessful()
        throw PrintMessage(
            if (isSuccessFull) {
                result.toString()
            } else {
                "we can't start the app: $result"
            },
            statusCode = if (isSuccessFull) 0 else 1,
            printError = !isSuccessFull
        )
    }

}
