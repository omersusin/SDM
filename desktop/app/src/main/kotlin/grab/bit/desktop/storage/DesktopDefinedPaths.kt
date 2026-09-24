package grab.bit.desktop.storage

import grab.bit.shared.util.DefinedPaths
import okio.Path
import java.io.File

class DesktopDefinedPaths(
    dataDir: Path
) : DefinedPaths(
    dataDir
) {
    val pageStatesStorageFile: Path = configDir.resolve("pageStatesStorage.json")
    val renderApiFile: Path = optionsDir.resolve("renderApi.txt")

    // optional read only properties file
    val appPropertiesFile = configDir.resolve("app.properties")
}
