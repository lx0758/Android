plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.http"
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
    api(libs.okhttp)
    api(libs.retrofit) {
        exclude(group = "com.squareup.okhttp3")
    }
    api(libs.retrofit.adapter.rxjava3) {
        exclude(group = "com.squareup.retrofit2")
        exclude(group = "io.reactivex.rxjava3")
        exclude(group = "org.reactivestreams")
    }
    api(libs.jackson.core)
    api(libs.retrofit.converter.jackson) {
        exclude(group = "com.squareup.retrofit2")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
    }
    compileOnly(libs.androidx.annotation)
}

//apply from: "../release.gradle"
//static def getLibraryArtifactId() {
//    return "http"
//}
//static def getLibraryVersionName() {
//    return "0.2.22"
//}
//static def getLibraryVersionCode() {
//    def version = getLibraryVersionName().replace("-SNAPSHOT", "")
//    def versions = version.split("\\.")
//    return Integer.parseInt(versions[0]) * 10000 + Integer.parseInt(versions[1]) * 100 + Integer.parseInt(versions[2]) * 1
//}