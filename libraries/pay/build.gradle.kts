plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.pay"
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
            withJavadocJar()
        }
    }
}

dependencies {
    api(fileTree(
        "dir" to "libs",
        "include" to "*.jar",
    ))
    api(libs.wechat.sdk)
    compileOnly(libs.androidx.fragment)
}

ext.apply {
    set("artifactId", "pay")
    set("version", "0.2.3")
}