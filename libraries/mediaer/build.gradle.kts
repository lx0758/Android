plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.mediaer"
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
    compileOnly(project(":libraries:http"))

    api(libs.chrisbanes.photoview)
    api(libs.yalantis.ucrop) {
        exclude(group = "com.squareup.okhttp3")
    }
    api(libs.glide)
    api(libs.glide.okhttp3.integration) {
        exclude(group = "com.squareup.okhttp3")
    }

    annotationProcessor(libs.glide.compiler)
}

//apply from: "../release.gradle"
//static def getLibraryArtifactId() {
//    return "mediaer"
//}
//static def getLibraryVersionName() {
//    return "0.2.2"
//}
//static def getLibraryVersionCode() {
//    def version = getLibraryVersionName().replace("-SNAPSHOT", "")
//    def versions = version.split("\\.")
//    return Integer.parseInt(versions[0]) * 10000 + Integer.parseInt(versions[1]) * 100 + Integer.parseInt(versions[2]) * 1
//}