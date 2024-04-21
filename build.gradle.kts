import org.jetbrains.kotlin.utils.addToStdlib.applyIf

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

val libraries = findProject(":libraries")
allprojects.forEach {
    it.applyIf(it.parent == libraries) {
        afterEvaluate {
            apply(from = rootProject.projectDir.path + "/gradle/release.gradle")
        }
    }
}