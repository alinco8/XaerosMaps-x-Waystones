pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Stonecutter - snapshots first for alpha versions
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.kikugie.dev/releases")

        // Modstitch
        maven("https://maven.isxander.dev/releases/")

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
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
        /**
         * @param mcVersion The base minecraft version.
         * @param loaders A list of loaders to target, supports "fabric" (1.14+), "neoforge"(1.20.6+), "vanilla"(any) or "forge"(<=1.20.1)
         */
        fun mc(mcVersion: String, loaders: List<String>, name: String = mcVersion) =
            loaders.forEach { version("$name-$it", mcVersion) }

        // Configure your targets here!
        mc("1.21.8", listOf("neoforge", "fabric"))
        mc("1.21.7", listOf("neoforge", "fabric"))
        mc("1.21.6", listOf("neoforge", "fabric"))
        mc("1.21.5", listOf("neoforge", "fabric"))
        mc("1.21.4", listOf("neoforge", "fabric"))
        mc("1.21.3", listOf("neoforge", "fabric"))
        mc("1.21.1", listOf("neoforge", "fabric"))

        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.1-neoforge"
    }
}



rootProject.name = "Xaero's Maps x Waystones"
