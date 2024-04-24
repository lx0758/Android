plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.util"
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
    compileOnly(libs.jackson.databind)

    testImplementation(project(":libraries:test"))
}

ext.apply {
    set("artifactId", "util")
    set("version", "0.2.8")
}