@file:Suppress("UnstableApiUsage")

import buildlogic.ifProp
import buildlogic.prop
import buildlogic.strictMaven
import org.slf4j.event.Level

plugins {
    id("project.common")
    id("net.neoforged.moddev") version "2.0.134"
    id("dev.kikugie.fletching-table.neoforge")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion: String by extra
val loaderName: String by extra

val localRuntime: Configuration = configurations.maybeCreate("localRuntime")

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

    listOf(
        "sodium",
        "lithium",
        "immediatelyfast",
        "ferrite-core",
        "modernfix",
        "badoptimizations"
    ).forEach {
        try {
            localRuntime(fletchingTable.modrinth(it))
        } catch (_: NoSuchElementException) {
            // Optional dependency
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