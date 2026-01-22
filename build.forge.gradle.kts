@file:Suppress("UnstableApiUsage")

import buildlogic.ifProp
import buildlogic.prop
import buildlogic.strictMaven
import org.slf4j.event.Level

plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.134"
    id("dev.kikugie.fletching-table.lexforge")
    id("me.modmuss50.mod-publish-plugin")
    id("project.common")
}

val mcVersion: String by extra
val loaderName: String by extra

val modLocalRuntime: Configuration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(modLocalRuntime)
    }
}

obfuscation {
    createRemappingConfiguration(modLocalRuntime)
}

repositories {
    strictMaven(
        "https://thedarkcolour.github.io/KotlinForForge/",
        "thedarkcolour"
    ) // Kotlin for Forge
}

dependencies {
    modImplementation("thedarkcolour:kotlinforforge:${prop("deps.kff")}")
    modImplementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}")
}

legacyForge {
    version = "$mcVersion-${prop("deps.forge")}"

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
        exclude("fabric.mod.json5", "META-INF/neoforge.mods.toml")
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
