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
    modImplementation("xaero.minimap:xaerominimap-$loaderName-$mcVersion:${prop("deps.xaeros_minimap")}")

    ifProp("deps.yacl") {
        modImplementation("dev.isxander:yet-another-config-lib:$it")
    }
}

sourceSets {
    named("main") {
        resources.setSrcDirs(listOf(layout.buildDirectory.dir("generated/stonecutter/main/resources")))
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

tasks {
    named<ProcessResources>("processResources") {
        dependsOn("stonecutterGenerate")

        val templateProps = getTemplateProps(loaderName)

        filesMatching(
            listOf(
                "**/*.toml",
                "**/*.json",
                "**/*.json5",
            )
        ) {
            filteringCharset = "UTF-8"
            expand(templateProps)
        }
    }
    named("sourcesJar") {
        dependsOn(named("stonecutterGenerate"))
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
    )

    ifProp("deps.yacl") {
        slugs.add("yacl")
    }

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