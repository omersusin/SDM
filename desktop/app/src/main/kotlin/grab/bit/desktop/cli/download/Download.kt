package grab.bit.desktop.cli.download

import grab.bit.desktop.cli.download.add.AddDownload
import grab.bit.desktop.cli.download.pause.PauseDownload
import grab.bit.desktop.cli.download.remove.RemoveDownload
import grab.bit.desktop.cli.download.resume.ResumeDownload
import grab.bit.desktop.cli.download.show.ShowDownload
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands

class Download : SuspendingCliktCommand("download") {
    init {
        subcommands(
            AddDownload(),
            ShowDownload(),
            RemoveDownload(),
            PauseDownload(),
            ResumeDownload(),
        )
    }

    override fun help(context: Context) = "Commands related to downloads"
    override suspend fun run() = Unit
}
