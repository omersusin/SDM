plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
    google()
}
version = 1
group = "grab.bit.plugin"
dependencies {
    implementation(libs.pluginAndroidGradle)
    implementation(libs.handlebarsJava)
    implementation(libs.okio.okio)
}
