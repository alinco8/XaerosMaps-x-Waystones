import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.token
import dev.isxander.modstitch.publishing.msPublishing

plugins {
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.isxander.modstitch.base") version "0.7.0-unstable"
    id("dev.kikugie.fletching-table") version "0.1.0-alpha.16"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    `maven-publish`

    kotlin("jvm") version "2.2.10"
}

fun <T> propMap(name: String, required: Boolean = false, block: (String) -> T?): T? =
    (System.getenv(name)
        ?: findProperty(name)?.toString()
            ?.takeIf { it.isNotBlank() })
        .let { if (required && it == null) error("Property $name is required ($minecraft/$loader)") else it }
        ?.let(block)

fun prop(name: String, required: Boolean = false): String? =
    propMap(name, required) { it }

val loader = when {
    modstitch.isLoom -> "fabric"
    modstitch.isModDevGradle -> "neoforge"
    else -> error("Unknown loader")
}
val modVersion: String by project
val modId: String by project
val minecraft = stonecutter.current.version

modstitch.apply {
    minecraftVersion = minecraft

    javaVersion = if (stonecutter.eval(minecraft, ">=1.20.5")) {
        21
    } else if (stonecutter.eval(minecraft, ">=1.18")) {
        17
    } else if (stonecutter.eval(minecraft, ">=1.17")) {
        16
    } else if (stonecutter.eval(minecraft, ">=1.12")) {
        8
    } else {
        error("Unknown java version for minecraft $minecraft")
    }

    // If parchment doesn't exist for a version, yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        propMap("deps.parchment") {
            mappingsVersion = it
            minecraftVersion = minecraft
        }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    val projectModId = modId
    val projectVersion = modVersion
    metadata {
        modId = projectModId
        modName = "Xaero's Maps x Waystones"
        modVersion = projectVersion
        modGroup = "dev.alinco8"
        modAuthor = "Alinco8"
        modDescription =
            "This mod adds compatibility between Xaero's Minimap/World Map and Waystones, allowing you to easily view the locations of your Waystones on the map."
        modLicense = "MIT"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mod_issue_tracker", "https://github.com/alinco8/XaerosMaps-x-Waystones/issues")
            put("mod_home_url", "https://github.com/alinco8/XaerosMaps-x-Waystones")
            put("mod_source_url", "https://github.com/alinco8/XaerosMaps-x-Waystones")

            put(
                "pack_format", when (minecraft) {
                    "1.16", "1.16.1" -> 5
                    "1.16.2", "1.16.3", "1.16.4", "1.16.5" -> 6
                    "1.17", "1.17.1" -> 7
                    "1.18", "1.18.1", "1.18.2" -> 8
                    "1.19", "1.19.1", "1.19.2" -> 9
                    "1.19.3", "1.19.4" -> 13
                    "1.20", "1.20.1" -> 15
                    "1.20.2" -> 18
                    "1.20.3", "1.20.4" -> 22
                    "1.20.5", "1.20.6" -> 32
                    "1.21", "1.21.1" -> 34
                    "1.21.2", "1.21.3" -> 42
                    "1.21.4" -> 46
                    "1.21.5" -> 55
                    "1.21.6" -> 63
                    "1.21.7", "1.21.8" -> 64

                    else -> throw IllegalArgumentException(
                        "Please store the resource pack version for $minecraft in build.gradle.kts! https://minecraft.wiki/w/Pack_format"
                    )
                }.toString()
            )
        }
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure it's up to date.
        fabricLoaderVersion = "0.17.2"

        // Configure loom like normal in this block.
        configureLoom {

        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        propMap("deps.forge") { forgeVersion = it }
        propMap("deps.neoform") { neoFormVersion = it }
        propMap("deps.neoforge") { neoForgeVersion = it }
        propMap("deps.mcp") { mcpVersion = it }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoForge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    /*mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register(modId)

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }*/
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
var constraint: String = name.split("-")[1]
stonecutter {
    constants.put("fabric", constraint == "fabric")
    constants.put("neoforge", constraint == "neoforge")
    constants.put("forge", constraint == "forge")
    constants.put("vanilla", constraint == "vanilla")
}

//fletchingTable {
//    lang.create("main") {
//        patterns.add("assets/lang/*.yml -> /assets/$modId/lang")
//    }
//    lang.all {
//        sortKeys = true
//        prettyPrint = true
//    }
//}
// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    fun modDependency(
        id: String,
        artifactGetter: (String) -> String,
        api: Boolean = false,
        supportsRuntime: Boolean = true,
        required: Boolean = true,
        version: String? = prop("deps.$id", required)
    ) {
        if (version == null) return
        if (version.isBlank()) error("Version for dependency $id is blank")
        val noRuntime = prop("deps.$id.no_runtime")?.toBoolean() ?: false
        require(noRuntime || supportsRuntime) {
            "No runtime dependency is not supported for $id"
        }

        artifactGetter(version).let {
            println(
                "Added dependency $it with ${
                    when (api to noRuntime) {
                        true to true -> {
                            modstitchModCompileOnlyApi(it)
                            "compileOnlyApi"
                        }

                        true to false -> {
                            modstitchModApi(it)
                            "api"
                        }

                        false to true -> {
                            modstitchModCompileOnly(it)
                            "compileOnly"
                        }

                        false to false -> {
                            modstitchModImplementation(it)
                            "implementation"
                        }

                        else -> error("Unreachable")
                    }
                }"
            )

            /*if (!noRuntime) {
                productionMods()
            }*/
        }
    }

    modstitch.loom {
//        modrinthDependency(
//            "fabric_api", "fabric-api",
//            api = true,
//        )
        modDependency(
            "fabric_api",
            { "net.fabricmc.fabric-api:fabric-api:$it" },
            api = true

        )


        modDependency(
            "kotlin",
            { "net.fabricmc:fabric-language-kotlin:$it" },
            version = "1.13.5+kotlin.2.2.10"
        )
//        runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:2.2.10")
        modDependency("modmenu", { "com.terraformersmc:modmenu:$it" })
    }
    modstitch.moddevgradle {
        modDependency("kotlin", { "thedarkcolour:kotlinforforge-neoforge:$it" }, api = true)
    }

    // Anything else in the dependencies block will be used for all platforms.
    modDependency("xaeros_minimap", { "maven.modrinth:xaeros-minimap:$it" })
    modDependency("xaeros_world_map", { "maven.modrinth:xaeros-world-map:$it" })
    modDependency("waystones", { "maven.modrinth:waystones:$it" })
    modDependency("balm", { "maven.modrinth:balm:$it" })
    modDependency("yacl", { "dev.isxander:yet-another-config-lib:$it" }, api = true)
}

publishMods {
    from(rootProject.publishMods)
    dryRun = rootProject.publishMods.dryRun

    file = modstitch.finalJarTask.flatMap { it.archiveFile }

    displayName = "$modVersion for $minecraft $loader"
    modLoaders.add(loader)

    fun versionList(versions: String) = prop(versions)
        ?.split(",")
        ?.map { it.trim() }
        ?: emptyList()

    val stableMcVersions = versionList("pub.stableMC")

    val modrinthId = prop("modrinth.id", true)!!
    modrinthId.isNotBlank().let {
        val token = prop("modrinth.token")

        if (token == null) {
            println("Modrinth token not found, skipping Modrinth publishing")
            return@let
        }

        modrinth {
            projectId.set(modrinthId)
            accessToken.set(token)
            minecraftVersions.add(minecraft)
            minecraftVersions.addAll(stableMcVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            announcementTitle =
                "Download $minecraft for ${loader.replaceFirstChar { it.uppercase() }} from Modrinth"

            when (loader) {
                "neoforge" -> {
                    requires { slug = "xaeros-minimap" }
                    requires { slug = "kotlin-for-forge" }
                    requires { slug = "waystones" }
                    requires { slug = "yacl" }
                }

                "fabric" -> {
                    requires { slug = "xaeros-minimap" }
                    requires { slug = "kotlin" }
                    requires { slug = "waystones" }
                    requires { slug = "balm" }
                    requires { slug = "yacl" }
                    requires { slug = "fabric-language-kotlin" }
                    requires { slug = "fabric-api" }
                }

                else -> error("Unknown loader $loader")
            }
        }
    }

    val curseforgeId = prop("curseforge.id", true)!!
    curseforgeId.isNotBlank().let {
        val token = prop("curseforge.token")

        if (token == null) {
            println("CurseForge token not found, skipping CurseForge publishing")
            return@let
        }

        curseforge {
            projectId = curseforgeId
            accessToken = token
            minecraftVersions.add(minecraft)
            minecraftVersions.addAll(stableMcVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            announcementTitle =
                "Download $minecraft for ${loader.replaceFirstChar { it.uppercase() }} from Modrinth"

            requires { slug.set("kotlin-for-forge") }
        }
    }
}