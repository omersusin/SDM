package grab.bit.updatechecker

import grab.bit.github.GithubApi
import grab.bit.ArtifactUtil
import io.github.z4kn4fein.semver.Version
import grab.bit.util.platform.Arch
import grab.bit.util.platform.Platform

class GithubUpdateChecker(
    currentVersion: Version,
    private val githubApi: GithubApi,
) : UpdateChecker(currentVersion) {
    override suspend fun getMyPlatformLatestVersion(): UpdateInfo {
        return getLatestVersionsForThisDevice()
    }

    private suspend fun getLatestVersionsForThisDevice(): UpdateInfo {
        val release = githubApi.getLatestReleases()
        val currentPlatform = Platform.getCurrentPlatform()
        val currentArch = Arch.getCurrentArch()
        val updateSources = mutableListOf<UpdateSource>()
        var foundVersion: Version? = null
        var initializedVersionFromAssetNames = false
        val hashLinks = release.assets
            .filter { it.name.endsWith(".sha256") || it.name.endsWith(".md5") }
            .groupBy { it.name.removeSuffix(".sha256").removeSuffix(".md5") }
            .mapValues { (_, assets) ->
                val best = assets.firstOrNull { it.name.endsWith(".sha256") } ?: assets.first()
                val algo = if (best.name.endsWith(".sha256")) "sha256" else "md5"
                best.downloadLink to algo
            }
        for (asset in release.assets) {
            val v = ArtifactUtil.getArtifactInfo(asset.name) ?: continue
            if (v.platform != currentPlatform) continue
            // universal builds should be installed on any arch
            if (!v.arch.isCompatible(currentArch)) continue
            if (!initializedVersionFromAssetNames) {
                foundVersion = v.version
                initializedVersionFromAssetNames = true
            }
            val isHashFile = asset.name.endsWith(".md5") || asset.name.endsWith(".sha256")
            if (!isHashFile) {
                updateSources.add(
                    UpdateSource.DirectDownloadLink(
                        link = asset.downloadLink,
                        name = asset.name,
                        hash = hashLinks[asset.name]?.let { (hashLink, algo) ->
                            githubApi.downloadText(hashLink)
                                ?.split(Regex("\\s+"))
                                ?.firstOrNull()
                                ?.takeIf { it.isNotBlank() }
                                ?.let { "$algo:$it" }
                        },
                        installableArch = v.arch,
                    )
                )
            }
        }
        return UpdateInfo(
            version = foundVersion
                ?: Version.parse(release.version.substring("v".length)),
            platform = currentPlatform,
            arch = currentArch,
            changeLog = release.body ?: "",
            updateSource = updateSources
        )
    }
}
