@file:Suppress("UnstableApiUsage")

import buildlogic.ifProp
import buildlogic.prop
import buildlogic.strictMaven
import org.slf4j.event.Level

plugins {
    id("net.neoforged.moddev") version "2.0.134"
    id("dev.kikugie.fletching-table.neoforge")
    id("me.modmuss50.mod-publish-plugin")
    id("project.common")
}

val mcVersion: String by extra
val loaderName: String by extra

val localRuntime: Configuration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}

repositories {
    strictMaven(
        "https://thedarkcolour.github.io/KotlinForForge/",
        "thedarkcolour"
    ) // Kotlin for Forge
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:${prop("deps.kff")}")

    implementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}") {
        exclude(group = "thedarkcolour", module = "kotlinforforge-neoforge")
    }

    listOf(
        "sodium",
        "lithium",
        "immediatelyfast",
        "ferrite-core",
        "modernfix",
        "badoptimizations",
    ).forEach {
        try {
            localRuntime(fletchingTable.modrinth(it))
        } catch (_: NoSuchElementException) {
            println("Mod '$it' not found in modrinth dependencies, skipping...")
        }
    }
}

neoForge {
    version = prop("deps.neoforge")

    ifProp("deps.parchment") {
        parchment {
            mappingsVersion = prop("deps.parchment")
            minecraftVersion = mcVersion
        }
    }

    runs {
        create("client") { client() }
        create("server") { server(); programArgument("--nogui") }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
        }
    }

    mods {
        create(prop("mod.id")) {
            sourceSet(sourceSets["main"])
        }
    }
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }
    named<ProcessResources>("processResources") {
        exclude("fabric.mod.json5", "META-INF/mods.toml")

        if (stonecutter.eval(mcVersion, "<=1.20.4")) {
            rename("""neoforge\.mods\.toml""", "mods.toml")
        }
    }
    named<Copy>("buildAndCollect") {
        from(jar.map { it.archiveFile }, sourcesJar.map { it.archiveFile })
    }
}

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").map { it.archiveFile.get() })

    val slugs = listOf("kotlin-for-forge")

    modrinth {
        slugs.forEach(::requires)
    }
    curseforge {
        slugs.forEach(::requires)
    }
}
