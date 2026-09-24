package grab.bit.resources

import grab.bit.resources.contracts.MyLanguageResource
import okio.FileSystem
import okio.Path.Companion.toPath

object ABDMLanguageResources {
    private const val LOCALES_DIRECTORY = "grab/bit/resources/locales/"
    val defaultLanguageResource = run {
        val defaultName = "en_US"
        MyLanguageResource.BundledLanguageResource(
            language = defaultName,
            getData = suspend { ResourceUtil.readResourceAsByteArray("$LOCALES_DIRECTORY$defaultName.properties") }
        )
    }
    val languages: List<MyLanguageResource>
        get() = ResourceMap
            .files
            .filter { it.startsWith(LOCALES_DIRECTORY) }
            .map {
                MyLanguageResource.BundledLanguageResource(
                    language = it.split("/").last().split(".").first(),
                    getData = suspend { ResourceUtil.readResourceAsByteArray(it) }
                )
            }

}

internal object ResourceUtil {
    fun readResourceAsByteArray(path: String): ByteArray {
        return FileSystem.RESOURCES.read(path.toPath()) {
            readByteArray()
        }
    }

    fun readResourceAsString(path: String): String {
        return FileSystem.RESOURCES.read(path.toPath()) {
            readUtf8()
        }
    }
}



object ABDMResources {
    fun getTranslatorsContent(): String {
        return ResourceUtil
            .readResourceAsString("grab/bit/resources/credits/translators.json")
    }
}
