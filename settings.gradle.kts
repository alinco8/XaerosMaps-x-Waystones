@file:Suppress("UnstableApiUsage")

rootProject.name = "Xaero's Maps x Waystones"
includeBuild("build-logic")

pluginManagement {
    repositories {
        maven("https://maven.neoforged.net/releases") // NeoForged
        maven("https://maven.fabricmc.net/") // Fabric
        maven("https://maven.kikugie.dev/snapshots") // Fletching Table
        maven("https://maven.kikugie.dev/releases") // Stonecutter
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.8.1"
}

stonecutter {
    create(rootProject) {
        fun mc(version: String, vararg loaders: String) = loaders.forEach {
            version("$version-$it", version).buildscript("build.$it.gradle.kts")
        }

        mc("1.20.1", "forge", "fabric") // 1.20..1.20.6
        mc("1.20.4", "neoforge") // 1.20.4..1.20.6
        mc("1.21.1", "neoforge", "fabric") // 1.21~1.21.10
        mc("1.21.11", "neoforge", "fabric") // 1.21.1

        vcsVersion = "1.21.1-neoforge"
    }
}
