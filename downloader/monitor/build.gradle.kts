plugins {
    id(MyPlugins.kotlinMultiplatform)
    id(Plugins.Kotlin.serialization)
    id(MyPlugins.composeBase)
    id(Plugins.Android.multiplatformLibrary)
}
kotlin {
    android {
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        namespace = "grab.bit.downloader.monitor"
        minSdk = 26
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":downloader:core"))
                implementation(project(":shared:utils"))
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.compose.runtime)
            }
        }
    }
}
