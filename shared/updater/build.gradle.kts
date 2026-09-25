plugins {
    id(MyPlugins.kotlinMultiplatform)
    id(Plugins.Android.multiplatformLibrary)
    id(Plugins.Kotlin.serialization)
}
kotlin {
    android {
        namespace = "grab.bit.updater"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = 26
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.serialization.json)
            api(libs.okhttp.okhttp)
            api(libs.kotlin.coroutines.core)
            implementation(project(":shared:utils"))
            implementation(libs.semver)
            implementation("ir.amirab.util:platform:1")
        }
    }
}
