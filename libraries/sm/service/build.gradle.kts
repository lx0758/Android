plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.sm"
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
    buildFeatures {
        aidl = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}
dependencies {
    compileOnly(libs.androidx.annotation)
    compileOnly(project(":libraries:sm:api"))
}

ext.apply {
    set("artifactId", "sm-service")
    set("version", "0.1.6")
}