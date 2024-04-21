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

include(":framework")

include(":librarys:abstracts")
include(":librarys:service")
include(":librarys:banner")
include(":librarys:downloader")
include(":librarys:http")
include(":librarys:list")
include(":librarys:pay")
include(":librarys:permission")
include(":librarys:mediaer")
include(":librarys:qrcode")
include(":librarys:io")
include(":librarys:tool")
include(":librarys:util")
include(":librarys:view")
include(":librarys:test")