plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.tool"
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
    compileOnly(libs.androidx.fragment)

    testImplementation(project(":librarys:test"))
    testImplementation(project(":librarys:util"))
}

//apply from: '../release.gradle'
//static def getLibraryArtifactId() {
//    return 'tool'
//}
//static def getLibraryVersionName() {
//    return '0.2.4'
//}
//static def getLibraryVersionCode() {
//    def version = getLibraryVersionName().replace("-SNAPSHOT", "")
//    def versions = version.split("\\.")
//    return Integer.parseInt(versions[0]) * 10000 + Integer.parseInt(versions[1]) * 100 + Integer.parseInt(versions[2]) * 1
//}