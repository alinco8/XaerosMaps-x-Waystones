plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
stonecutter active "1.21.1-fabric"

val modVersion = System.getenv("MOD_VERSION") ?: property("mod.version")!!.toString()

// タスク実行前にchangelogを取得
val changelogContent: String by lazy {
    System.getenv("CHANGELOG") ?: run {
        if (gradle.startParameter.taskNames.any { it.contains("publish", ignoreCase = true) }) {
            println("Enter changelog (press Ctrl+D when finished):")
            System.`in`.bufferedReader().use { it.readText() }.trim().also {
                if (it.isEmpty()) {
                    throw GradleException("CHANGELOG is required")
                }
            }
        } else {
            ""
        }
    }
}

publishMods {
    dryRun = System.getenv("DRY_RUN")?.toBoolean() ?: true

    version = modVersion

    changelog = """
# Xaero's Maps x Waystones v$modVersion
$changelogContent
""".trimIndent()

    type = when {
        "alpha" in modVersion.lowercase() -> ALPHA
        "beta" in modVersion.lowercase() -> BETA
        else -> STABLE
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()

        // NeoForge
        maven("https://maven.neoforged.net/releases")
        // Fabric
        maven("https://maven.fabricmc.net/")
        // Forge
        maven("https://files.minecraftforge.net/maven")

        // Stonecutter, FletchingTable
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

        // Placeholder API
        maven("https://maven.nucleoid.xyz/")
    }
}