import org.jetbrains.kotlin.utils.addToStdlib.applyIf

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

allprojects.forEach { project ->
    project.applyIf(project.path.startsWith(":libraries") && project.childProjects.isEmpty()) {
        afterEvaluate {
            apply(from = rootProject.projectDir.path + "/gradle/release.gradle")
        }
    }
}