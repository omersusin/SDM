package buildlogic

import io.github.z4kn4fein.semver.Version
import grab.bit.installer.InstallerTargetFormat
import grab.bit.util.platform.Arch
import grab.bit.util.platform.Platform
import org.gradle.api.Project
import java.io.File

object CiUtils {
    fun getTargetFileName(
        packageName: String,
        appVersion: Version,
        target: InstallerTargetFormat?,
        archName: String?,
    ): String {
        val fileExtension = when (target) {
            // we use archived for app image distribution ( app image is a folder actually so there is no installer so we zip it instead)
            null -> {
                when (Platform.getCurrentPlatform()) {
                    Platform.Desktop.Linux -> "tar.gz"
                    Platform.Desktop.MacOS -> "tar.gz"
                    Platform.Desktop.Windows -> "zip"
                    Platform.Android -> error("this can only be used with desktop formats")
                }
            }

            else -> target.fileExtensionWithoutDot()
        }

        val platformName = when (target) {
            null -> Platform.getCurrentPlatform()
            else -> {
                val packageFileExt = target.fileExtensionWithoutDot()
                requireNotNull(Platform.fromExecutableFileExtension(packageFileExt)) {
                    "can't find platform name with this file extension: ${packageFileExt}"
                }
            }
        }.name.lowercase()
        val nameWithoutExtension = listOf(
            packageName,
            appVersion.toString(),
            platformName,
            archName?: "universal",
        ).joinToString("_")
        return "$nameWithoutExtension.${fileExtension}"
    }

    fun getFileOfPackagedTarget(
        baseOutputDir: File,
        target: InstallerTargetFormat,
    ): File {
        val folder = baseOutputDir
//        val folder = baseOutputDir.resolve(target.outputDirName)
        val exeFile = kotlin.runCatching {
            folder.walk().first {
                it.name.endsWith(target.fileExt)
            }
        }.onFailure {
            println("error when finding packaged app for $target in: $baseOutputDir")
        }
        return exeFile.getOrThrow()
    }

    fun getFileOfDistributedArchivedTarget(
        baseOutputDir: File,
    ): File {
        val folder = baseOutputDir
        val extension = when (Platform.getCurrentPlatform()) {
            Platform.Desktop.Linux,
            Platform.Desktop.MacOS -> "tar.gz"

            Platform.Android,
            Platform.Desktop.Windows -> "zip"
        }
        val archiveFile = kotlin.runCatching {
            folder.walk().first {
                it.name.endsWith(extension)
            }
        }.onFailure {
            println("error when finding archive of unpackaged app in: $baseOutputDir")
        }
        return archiveFile.getOrThrow()
    }

    fun copyAndHashToDestination(
        src: File,
        destinationFolder: File,
        name: String,
    ) {
        val destinationExeFile = destinationFolder.resolve(name)
        src.copyTo(destinationExeFile)
        val md5File = destinationFolder.resolve("$name.md5")
        md5File.writeText(HashUtils.md5(src))
    }

    fun movePackagedAndCreateSignature(
        appVersion: Version,
        packageName: String,
        target: InstallerTargetFormat,
        basePackagedAppsDir: File,
        outputDir: File,
    ) {
        require(!outputDir.isFile) {
            "$outputDir is a file"
        }
        outputDir.mkdirs()
        require(outputDir.isDirectory) {
            "$outputDir is not directory"
        }

        val exeFile = getFileOfPackagedTarget(
            baseOutputDir = basePackagedAppsDir,
            target = target
        )
        val arch = Arch.getCurrentArch().name
        val newName = getTargetFileName(
            packageName = packageName,
            appVersion = appVersion,
            target = target,
            archName = arch,
        )
        copyAndHashToDestination(
            src = exeFile,
            destinationFolder = outputDir,
            name = newName,
        )
    }

    fun getCiDir(project: Project): CiDirs {
        return CiDirs(project.rootProject.layout.buildDirectory)
    }
    fun getCreateBinaryFolderForCiTaskName(): String {
        return "createBinariesForCi"
    }
}


private fun InstallerTargetFormat.fileExtensionWithoutDot() = fileExt.substring(".".length)
