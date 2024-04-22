import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.liux.android.example"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()
    signingConfigs {
        create("config") {
            val properties = loadProperties(rootProject.file("local.properties").path)
            val signStorePath = properties.getProperty("sign.StorePath")
            val signStorePassword = properties.getProperty("sign.StorePassword")
            val signKeyAlias = properties.getProperty("sign.KeyAlias")
            val signKeyPassword = properties.getProperty("sign.KeyPassword")

            storeFile = file(signStorePath)
            storePassword = signStorePassword
            keyAlias = signKeyAlias
            keyPassword = signKeyPassword
        }
    }
    defaultConfig {
        applicationId = "com.liux.android.example"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1_00_00
        versionName = "1.0.0.0000"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("config")
        }
        release {
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("config")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
}

dependencies {
    implementation(fileTree(
        "dir" to "libs",
        "include" to "*.jar",
    ))

    implementation(project(":libraries:abstracts"))
    implementation(project(":libraries:banner"))
    implementation(project(":libraries:downloader"))
    implementation(project(":libraries:http"))
    implementation(project(":libraries:list"))
    implementation(project(":libraries:tool"))
    implementation(project(":libraries:pay"))
    implementation(project(":libraries:permission"))
    implementation(project(":libraries:mediaer"))
    implementation(project(":libraries:qrcode"))
    implementation(project(":libraries:io"))
    implementation(project(":libraries:service"))
    implementation(project(":libraries:util"))
    implementation(project(":libraries:view"))

    implementation(libs.rxandroid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
