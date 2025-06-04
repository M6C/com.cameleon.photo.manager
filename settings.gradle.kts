pluginManagement {
    plugins {
        id("com.android.application") version "8.3.1"
        id("com.android.library") version "8.3.1"
        id("org.jetbrains.kotlin.multiplatform") version "1.9.22"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
//        id("org.jetbrains.kotlin.native.cocoapods") version "1.9.22"
        id("com.google.dagger.hilt.android") version "2.51.1"
        id("com.google.gms.google-services") version "4.4.1"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Photo Manager"
include(":app")
include(":shared")

