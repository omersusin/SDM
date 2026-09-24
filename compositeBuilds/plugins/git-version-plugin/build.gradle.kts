plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}
version = 1
group = "grab.bit.plugin"
dependencies {
    implementation(libs.semver)
    implementation(libs.jgit)
}
gradlePlugin {
    plugins {
        create("git-version-plugin") {
            id = "grab.bit.git-version-plugin"
            implementationClass = "grab.bit.git_version.GitVersionPlugin"
        }
    }
}