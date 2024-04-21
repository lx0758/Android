plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.abstracts"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        debug {

        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(libs.androidx.appcompat)
}

//apply("../release.gradle.kts")
//fun getLibraryArtifactId(): String {
//    return "abstracts"
//}
//fun getLibraryVersionName(): String {
//    return "0.2.4"
//}
//fun getLibraryVersionCode(): Int {
//    val version = getLibraryVersionName().replace("-SNAPSHOT", "")
//    val versions = version.split("\\.")
//    return versions[0].toInt() * 10000 + versions[1].toInt() * 100 + versions[2].toInt() * 1
//}