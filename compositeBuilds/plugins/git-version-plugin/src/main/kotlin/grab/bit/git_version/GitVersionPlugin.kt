package grab.bit.git_version

import grab.bit.git_version.core.GitVersionExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger

class GitVersionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val gitVersionExtension = GitVersionExtension()
        target.extensions.add("gitVersion", gitVersionExtension)
        gitVersionExtension.currentWorkingDirectory = target.rootDir
        gitVersionExtension.setLogger(target.logger)
    }
}