@file:Suppress("UnstableApiUsage")

import buildlogic.ifProp
import buildlogic.prop
import buildlogic.strictMaven

plugins {
    id("dev.kikugie.loom-back-compat")
    id("dev.kikugie.fletching-table.fabric")
    id("me.modmuss50.mod-publish-plugin")
    id("project.common")
}

val mcVersion: String by extra
val loaderName: String by extra

fletchingTable {
    j52j.register("main") {
        extension("json", "fabric.mod.json5")
    }
    j52j.all {
        prettyPrint = true
    }
}

repositories {
    strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") // Mod Menu
}

dependencies {
    // TODO: Add parchment mappings
    loomx.applyMojangMappings()

    minecraft("com.mojang:minecraft:$mcVersion")
    modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${prop("deps.fabric_kotlin")}")

    modImplementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
    modImplementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}")

    listOf(
        "sodium",
        "lithium",
        "immediatelyfast",
        "ferrite-core",
        "modernfix",
        "badoptimizations"
    ).forEach {
        try {
            modLocalRuntime(fletchingTable.modrinth(it))
        } catch (_: NoSuchElementException) {
            // Optional dependency
        }
    }
}

loom {
    runs {
        configureEach {
            vmArgs(
                "-Dsodium.checks.issue2561=false",
            )
            property("fabric.log.level", "debug")
        }
    }
}

tasks {
    named<ProcessResources>("processResources") {
        exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml")
    }
    named<Copy>("buildAndCollect") {
        from(loomx.modJar.map { it.archiveFile }, loomx.modSourcesJar.map { it.archiveFile })
    }
}

publishMods {
    file = loomx.modJar.map { it.archiveFile.get() }
    additionalFiles.from(loomx.modSourcesJar.map { it.archiveFile.get() })

    val slugs = listOf("fabric-api", "fabric-language-kotlin")
    val optionalSlugs = listOf("modmenu")

    modrinth {
        slugs.forEach(::requires)
        optionalSlugs.forEach(::optional)
    }
    curseforge {
        slugs.forEach(::requires)
        optionalSlugs.forEach(::optional)
    }
}
