import buildlogic.getTemplateProps
import buildlogic.ifProp
import buildlogic.modImplementation
import buildlogic.prop
import buildlogic.propOrNull
import buildlogic.strictMaven

plugins {
    kotlin("jvm")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.kikugie.fletching-table")
}

repositories {
    strictMaven("https://maven.isxander.dev/releases", "dev.isxander") // YACL
    strictMaven("https://maven.quiltmc.org/repository/release", "org.quiltmc") // QuiltMC
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth") // Modrinth
    strictMaven("https://maven.parchmentmc.org/", "org.parchmentmc") // Parchment
    strictMaven("https://chocolateminecraft.com/maven", "xaero") // Xaero Lib
    mavenCentral()
}

val (mcVersion, loaderName) = project.name.split("-")
extra["mcVersion"] = mcVersion
extra["loaderName"] = loaderName

base.archivesName = prop("mod.id")
version = "${prop("mod.version")}+$mcVersion-$loaderName"

fletchingTable {
    lang.create("main") {
        patterns.add("assets/${prop("mod.id")}/lang/**")
    }
    lang.all {
        sortKeys = true
        prettyPrint = true
    }
}

dependencies {
    fletchingTable.minecraft = mcVersion

    modImplementation("maven.modrinth:waystones:${prop("deps.waystones")}")
    modImplementation("maven.modrinth:balm:${prop("deps.balm")}")

    val minimapParts = prop("deps.xaeros_minimap").split('-').reversed()
    val minimapMc = minimapParts.getOrNull(1)
    val minimapVersion = minimapParts[0]
    modImplementation("xaero.minimap:xaerominimap-$loaderName-${minimapMc ?: mcVersion}:$minimapVersion")

    val worldMapParts = prop("deps.xaeros_world_map").split('-').reversed()
    val worldMapMc = worldMapParts.getOrNull(1)
    val worldMapVersion = worldMapParts[0]
    modImplementation("xaero.map:xaeroworldmap-$loaderName-${worldMapMc ?: mcVersion}:$worldMapVersion")

    val libParts = prop("deps.xaeros_lib").split('-').reversed()
    val libMc = libParts.getOrNull(1)
    val libVersion = libParts[0]
    modImplementation("xaero.lib:xaerolib-$loaderName-${libMc ?: mcVersion}:$libVersion")
}

sourceSets {
    named("main") {
        resources.setSrcDirs(listOf(layout.buildDirectory.dir("generated/stonecutter/main/resources")))
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(prop("deps.java").toInt())
    }
}

kotlin {
    jvmToolchain(prop("deps.java").toInt())
}

tasks {
    named<ProcessResources>("processResources") {
        dependsOn("stonecutterGenerate")

        val templateProps = getTemplateProps(mcVersion, loaderName)

        inputs.property("templateProps", templateProps)
        filesMatching(
            listOf(
                "**/*.toml",
                "**/*.json",
                "**/*.json5",
                "**/*.mcmeta",
            )
        ) {
            filteringCharset = "UTF-8"
            expand(templateProps)
        }
    }
    named("sourcesJar") {
        dependsOn(named("stonecutterGenerate"))
    }
    register<Copy>("buildAndCollect") {
        dependsOn(named("build"))
        group = "build"
        into(rootProject.layout.buildDirectory.file("libs"))
    }
}

publishMods {
    dryRun = propOrNull("DRY_RUN")?.toBoolean() ?: true
    type = STABLE
    version = project.version.toString()
    modLoaders.add(loaderName)
    changelog = propOrNull("CHANGELOG") ?: "No changelog provided."

    displayName = "${prop("mod.version")} for $loaderName $mcVersion"

    var mcVersions = ((propOrNull("pub.minecraft")
        ?.split(" ") ?: listOf()) + mcVersion).distinct()

    var slugs = mutableListOf(
        "waystones",
        "xaeros-minimap",
        "yacl"
    )

    ifProp("MODRINTH_TOKEN") {
        modrinth {
            projectId = prop("pub.modrinth.id")
            accessToken = it
            minecraftVersions.addAll(mcVersions)

            slugs.forEach(::requires)
        }
    }
    ifProp("CURSEFORGE_TOKEN") {
        curseforge {
            projectId = prop("pub.curseforge.id")
            accessToken = it
            minecraftVersions.addAll(mcVersions)

            clientRequired

            slugs.forEach(::requires)
        }
    }
}
