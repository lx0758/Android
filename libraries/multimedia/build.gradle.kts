plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.multimedia"
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
    compileOnly(libs.okhttp)
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

ext.apply {
    set("artifactId", "multimedia")
    set("version", "0.2.3")
}