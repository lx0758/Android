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
            withJavadocJar()
        }
    }
}

dependencies {
    compileOnly(libs.androidx.fragment)

    testImplementation(project(":libraries:test"))
    testImplementation(project(":libraries:util"))
}

ext.apply {
    set("artifactId", "tool")
    set("version", "0.2.9")
}