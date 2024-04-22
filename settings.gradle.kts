@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io/")
        maven("https://jcenter.bintray.com/")
        mavenLocal()
    }
}

rootProject.name = "Android"

include(":example")

include(":libraries:abstracts")
include(":libraries:service")
include(":libraries:banner")
include(":libraries:downloader")
include(":libraries:http")
include(":libraries:list")
include(":libraries:pay")
include(":libraries:permission")
include(":libraries:mediaer")
include(":libraries:qrcode")
include(":libraries:io")
include(":libraries:tool")
include(":libraries:util")
include(":libraries:view")
include(":libraries:test")