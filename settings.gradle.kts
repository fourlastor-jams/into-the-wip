@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":desktop")
include(":core")
include(":html")
include(":teavm")

dependencyResolutionManagement {
    versionCatalogs { create("libs") }
}
