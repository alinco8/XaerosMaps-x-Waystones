pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Stonecutter, FletchingTable
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")

        // Modstitch
        maven("https://maven.isxander.dev/releases/")

        // Loom
        maven("https://maven.fabricmc.net/")

        // ModDevGradle
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7+"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        // "fabric" (1.14+), "neoforge"(1.20.6+), "vanilla"(any) or "forge"(<=1.20.1)
        fun mc(mcVersion: String, loaders: List<String>, name: String = mcVersion) =
            loaders.forEach { version("$name-$it", mcVersion) }

        mc("1.21.1", listOf("neoforge", "fabric"))
//        mc("1.20.1", listOf("forge", "fabric"))
//        mc("1.19.2", listOf("forge", "fabric"))
//        mc("1.18.2", listOf("forge", "fabric"))
//        mc("1.16.5", listOf("forge"))

        // Default target
        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = "Xaero's Maps x Waystones"
