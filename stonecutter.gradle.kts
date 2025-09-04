plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
stonecutter active "1.21.1-fabric"

/*stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}*/

publishMods {
    val modVersion: String by project
    version = modVersion

    val modChangelog = provider {
        rootProject.file("CHANGELOG.md").takeIf { it.exists() }
            ?.readText()
            ?.replace("{version}", modVersion)
            ?.replace("{targets}", stonecutter.versions.joinToString("\n") { "- $it" })
            ?: "No changelog provided."
    }
    changelog.set(modChangelog)

    type.set(
        when {
            "alpha" in modVersion.lowercase() -> ALPHA
            "beta" in modVersion.lowercase() -> BETA
            else -> STABLE
        }
    )
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()

        // NeoForge
        maven("https://maven.neoforged.net/releases")
        // Fabric
        maven("https://maven.fabricmc.net/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        // Kotlin for Forge
        maven("https://thedarkcolour.github.io/KotlinForForge/")

        // Yet Another Config Lib
        maven("https://maven.isxander.dev/releases")

        // Mod Menu
        maven("https://maven.terraformersmc.com/")

        // Quilt
        maven("https://maven.quiltmc.org/repository/release/")
    }
}