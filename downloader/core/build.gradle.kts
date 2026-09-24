
plugins {
    id(MyPlugins.kotlinMultiplatform)
    id(Plugins.Kotlin.serialization)
    id(Plugins.Android.multiplatformLibrary)
}
kotlin {
    jvm("desktop")
    android {
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        namespace = "ir.amirab.downloader.core"
        minSdk = 26
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.coroutines.core)
                api(libs.okio.okio)
                api(libs.okhttp.okhttp)
                api(libs.okhttp.coroutines)
                implementation(project(":shared:utils"))
                api("io.lindstrom:m3u8-parser:0.29")
            }
        }
        androidMain.dependencies {
            implementation(libs.libtorrent4j)
            implementation(libs.libtorrent4j.android.arm)
            implementation(libs.libtorrent4j.android.arm64)
            implementation(libs.libtorrent4j.android.x86)
            implementation(libs.libtorrent4j.android.x86.x4)
            implementation(libs.youtubedl.android.library)
        }
        val desktopMain = getByName("desktopMain")
        desktopMain.dependencies {
            implementation(libs.libtorrent4j)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
