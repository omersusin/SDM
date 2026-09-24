plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
}
version = 1
group = "grab.bit.plugin"
dependencies {
    implementation("ir.amirab.util:platform:1")
    implementation(libs.handlebarsJava)
}
gradlePlugin {
    plugins {
        create("installer-plugin") {
            id = "grab.bit.installer-plugin"
            implementationClass = "grab.bit.installer.InstallerPlugin"
        }
    }
}
