@file:Suppress("UnstableApiUsage")

import buildlogic.ifProp
import buildlogic.prop
import buildlogic.strictMaven

plugins {
    id("fabric-loom") version "1.14-SNAPSHOT"
    id("project.common")
    id("dev.kikugie.fletching-table.fabric")
    id("me.modmuss50.mod-publish-plugin")
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
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        officialMojangMappings()
        ifProp("deps.parchment") {
            parchment("org.parchmentmc.data:parchment-$mcVersion:$it")
        }
    })
    modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric_api")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${prop("deps.fabric_kotlin")}")

    modImplementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")

    ifProp("deps.yacl") {
        modImplementation("dev.isxander:yet-another-config-lib:$it")
    }

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
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
    }
}

publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

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
