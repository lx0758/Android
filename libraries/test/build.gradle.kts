plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.liux.android.test"
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
    testOptions {
        unitTests { 
            isIncludeAndroidResources = true
        }
        unitTests.all {
            // http://robolectric.org/configuring/#system-properties
            it.systemProperty("robolectric.dependency.repo.id", "central")
            it.systemProperty("robolectric.dependency.repo.url", "https://repo1.maven.org/maven2/")
            it.systemProperty("robolectric.dependency.repo.username", "")
            it.systemProperty("robolectric.dependency.repo.password", "")
            it.systemProperty("robolectric.dependency.proxy.host", project.findProperty("systemProp.https.proxyHost") ?: System.getenv("ROBOLECTRIC_PROXY_HOST"))
            it.systemProperty("robolectric.dependency.proxy.port", project.findProperty("systemProp.https.proxyPort") ?: System.getenv("ROBOLECTRIC_PROXY_PORT"))
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
    api(libs.junit)
    api(libs.androidx.test)
    api(libs.robolectric)
    api(libs.mockito)
}

ext.apply {
    set("artifactId", "test")
    set("version", "0.1.4")
}