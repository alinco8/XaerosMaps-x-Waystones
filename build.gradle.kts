plugins {
    id("com.google.devtools.ksp") version "2.3.3"
    id("dev.isxander.modstitch.base") version "0.7.1-unstable"
    id("dev.kikugie.fletching-table") version "0.1.0-alpha.22"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("com.github.ben-manes.versions") version "0.53.0"
    `maven-publish`

    kotlin("jvm") version "2.3.0-RC"
}

fun <T> ifPresent(name: String, required: Boolean = false, block: (String) -> T?): T? =
    (System.getenv(name) ?: findProperty(name)
        ?.toString()?.takeIf { it.isNotBlank() })
        .let { if (required && it == null) error("Property $name is required") else it }
        ?.let(block)

fun prop(name: String, required: Boolean = false): String? =
    ifPresent(name, required) { it }

val mcVersion = stonecutter.current.version
val mcRange = prop("mcRange") ?: mcVersion
val loaderName = when {
    modstitch.isLoom -> "fabric"
    modstitch.isModDevGradle -> "neoforge"
    modstitch.isModDevGradleLegacy -> "forge"
    else -> error("Unknown loader: $this")
}

val modId = prop("mod.id", true)!!
val modVersion = System.getenv("MOD_VERSION") ?: property("mod.version")!!.toString()
val modRepo = prop("mod.repo", true)!!

modstitch.apply {
    minecraftVersion = mcVersion
    javaVersion = listOf(
        ("1.20.5" to 21),
        ("1.18" to 17),
        ("1.17" to 16),
        ("1.12" to 8)
    ).firstOrNull { (ver, _) ->
        stonecutter.eval(mcVersion, ">=$ver")
    }?.second ?: error("Unknown java version for minecraft $mcVersion")


    finalJarTask {
        archiveFileName = "$modId-$modVersion+$mcRange-$loaderName.jar"
    }

    parchment {
        ifPresent("deps.parchment") {
            // optional

            mappingsVersion = it
            minecraftVersion = mcVersion
        }
    }

    metadata {
        modId = prop("mod.id", true)!!
        modVersion = System.getenv("MOD_VERSION") ?: property("mod.version")!!.toString()
        modName = "Xaero's Maps x Waystones"
        modDescription =
            "This mod adds compatibility between Xaero's Minimap/World Map and Waystones, allowing you to easily view the locations of your Waystones on the map."
        modGroup = "dev.alinco8.xaeromaps_waystones"
        modAuthor = "alinco8"
        modLicense = "MIT"

        replacementProperties = mapOf(
            "mod_issue_tracker" to "$modRepo/issues",
            "mod_home_url" to modRepo,
            "mod_source_url" to modRepo,
            "pack_format" to when (mcVersion) {
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
                "1.21.9", "1.21.10" -> 65
                else -> throw IllegalArgumentException(
                    "Please store the resource pack version for $mcVersion in build.gradle.kts!"
                )
            }.toString()
        )

    }

    loom {
        fabricLoaderVersion = "0.17.3"

        configureLoom {}
    }

    moddevgradle {
        ifPresent("deps.forge") { forgeVersion = it }
        ifPresent("deps.neoform") { neoFormVersion = it }
        ifPresent("deps.neoforge") { neoForgeVersion = it }
        ifPresent("deps.mcp") { mcpVersion = it }

        defaultRuns()

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

stonecutter {
    constants.put("fabric", loaderName == "fabric")
    constants.put("neoforge", loaderName == "neoforge")
    constants.put("forge", loaderName == "forge")
    constants.put("yacl", prop("deps.yacl", false) != null)

    dependencies["waystones"] = prop("deps.waystones", true)!!.split("+").first()
}

fletchingTable {
    lang.create("main") {
        patterns.add("assets/lang/*.yml -> /assets/$modId/lang")
    }
    lang.all {
        sortKeys = true
        prettyPrint = true
    }
}

dependencies {
    fun modDependency(
        id: String,
        artifactGetter: (String) -> String,
        api: Boolean = false,
        withRuntime: Boolean = true,
        required: Boolean = true,
        version: String? = null,
    ) {
        val version = if (version == null) {
            val version = prop("deps.$id", required)
            if (version == null || version.isBlank()) {
                if (required) error("Dependency $id is required")
                println("Optional dependency $id not found")
                return
            }
            version
        } else {
            version
        }
        artifactGetter(version).let {
            when (api to withRuntime) {
                true to false -> {
                    modstitchModCompileOnlyApi(it)
                }

                true to true -> {
                    modstitchModApi(it)
                }

                false to false -> {
                    modstitchModCompileOnly(it)
                }

                false to true -> {
                    modstitchModImplementation(it)
                }

                else -> error("Unreachable")
            }
        }
    }

    fun modrinthDependency(
        slug: String,
        api: Boolean = false,
        withRuntime: Boolean = true,
        required: Boolean = true,
    ) {
        val dependency = try {
            fletchingTable.modrinth(slug, mcVersion, loaderName)
        } catch (err: Exception) {
            if (required) throw err
            println("Optional modrinth dependency $slug not found for $mcVersion $loaderName")
            return
        }

        modDependency(
            "",
            { "maven.modrinth:$slug:${it}" },
            api = api,
            withRuntime = withRuntime,
            required = required,
            version = dependency.version
        )
    }

    modstitch.loom {
        modDependency(
            "fabric_api",
            { "net.fabricmc.fabric-api:fabric-api:$it" },
            api = true
        )
        modDependency(
            "kotlin",
            { "net.fabricmc:fabric-language-kotlin:$it" },
            version = "1.13.6+kotlin.2.2.20"
        )
        modDependency("modmenu", { "com.terraformersmc:modmenu:$it" })
    }
    modstitch.moddevgradle {
        modDependency(
            "kotlin",
            { "thedarkcolour:kotlinforforge-neoforge:$it" },
            api = true
        )
    }

    // Required dependencies
    modrinthDependency("xaeros-minimap")
    modrinthDependency("waystones")
    modrinthDependency("balm")

    // Optional dependencies
    modDependency(
        "yacl",
        { "dev.isxander:yet-another-config-lib:$it" },
        api = true,
        required = false
    )
}

publishMods {
    from(rootProject.publishMods)

    dryRun = rootProject.publishMods.dryRun
    displayName = "$modVersion for $mcRange $loaderName"
    modLoaders.add(loaderName)

    modstitch.onEnable {
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
    }

    fun versionList(versions: String) = prop(versions)
        ?.split(",")
        ?.map { it.trim() }
        ?: emptyList()

    val stableMcVersions = versionList("pub.stableMC")

    val sharedSlugs = listOf(
        "xaeros-minimap",
        "waystones",
        "yacl",
    )
    val neoSlugs = listOf(
        "kotlin-for-forge",
    )
    val fabricSlugs = listOf(
        "fabric-language-kotlin",
        "fabric-api",
    )

    val modrinthId = prop("modrinth.id", true)!!
    modrinthId.isNotBlank().let {
        val token = prop("modrinth.token") ?: return@let

        modrinth {
            projectId = modrinthId
            accessToken = token
            minecraftVersions.add(mcVersion)
            minecraftVersions.addAll(stableMcVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            announcementTitle =
                "Download $mcRange for ${loaderName.replaceFirstChar { it.uppercase() }} from Modrinth"

            when (loaderName) {
                "neoforge" -> {
                    sharedSlugs.forEach { requires { slug = it } }
                    neoSlugs.forEach { requires { slug = it } }
                }

                "fabric" -> {
                    sharedSlugs.forEach { requires { slug = it } }
                    fabricSlugs.forEach { requires { slug = it } }
                }

                else -> error("Unknown loader $loaderName")
            }

            println("Modrinth publishing is successfully configured")
        }
    }

    val curseforgeId = prop("curseforge.id", true)!!
    curseforgeId.isNotBlank().let {
        val token = prop("curseforge.token") ?: return@let

        curseforge {
            projectId = curseforgeId
            accessToken = token
            minecraftVersions.add(mcVersion)
            minecraftVersions.addAll(stableMcVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            clientRequired
            serverRequired

            announcementTitle =
                "Download $mcRange for ${loaderName.replaceFirstChar { it.uppercase() }} from Modrinth"

            when (loaderName) {
                "neoforge" -> {
                    sharedSlugs.forEach { requires { slug = it } }
                    neoSlugs.forEach { requires { slug = it } }
                }

                "fabric" -> {
                    sharedSlugs.forEach { requires { slug = it } }
                    fabricSlugs.forEach { requires { slug = it } }
                }

                else -> error("Unknown loader $loaderName")
            }

            println("CurseForge publishing is successfully configured")
        }
    }
}