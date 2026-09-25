
plugins {
    id(MyPlugins.kotlinMultiplatform)
    id(Plugins.Android.multiplatformLibrary)
}
kotlin {
    android {
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        namespace = "grab.bit.util.startup"
        minSdk = 26
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:utils"))
        }
    }
}
