plugins {
    id(MyPlugins.kotlinMultiplatform)
    id(Plugins.Kotlin.serialization)
    id(Plugins.Android.multiplatformLibrary)
}
kotlin {
    android {
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        namespace = "grab.bit.util"
        minSdk = 26
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.serialization.json)
            api(libs.okio.okio)
            api(libs.okhttp.okhttp)
            api(libs.kotlin.coroutines.core)
            api(libs.kotlin.datetime)
            api(libs.semver)
            api(libs.arrow.optics)
            api(libs.kermit)
            api("ir.amirab.util:platform:1")
        }
        androidMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.androidx.core.ktx)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
