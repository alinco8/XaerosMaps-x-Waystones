import buildlogic.getTemplateProps
import buildlogic.ifProp
import buildlogic.modImplementation
import buildlogic.prop
import buildlogic.propOrNull
import buildlogic.strictMaven

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.kikugie.fletching-table")
}

repositories {
    maven("https://maven.isxander.dev/releases") { // YACL (1.20.x)
        content {
            includeVersionByRegex(
                "dev.isxander",
                "yet-another-config-lib",
                "^[^+]+\\+1\\.20.*"
            )
        }
    }
    strictMaven("https://maven.quiltmc.org/repository/release", "org.quiltmc") // QuiltMC
    strictMaven("https://api.modrinth.com/maven", "maven.modrinth") // Modrinth
    strictMaven("https://beta.cursemaven.com", "curse.maven") // CurseMaven
    strictMaven("https://maven.parchmentmc.org/", "org.parchmentmc") // Parchment
    strictMaven("https://chocolateminecraft.com/maven", "xaero") // Xaero Lib
    mavenCentral() // YACL & Other
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

    mixins.create("main") {
        mixin("default", "xmxw.mixins.json") {
            env("CLIENT")
        }
    }
}

dependencies {
    fletchingTable.minecraft = mcVersion

    if (prop("deps.waystones.version").contains('.')) {
        modImplementation("maven.modrinth:waystones:${prop("deps.waystones.version")}")
    } else {
        modImplementation("curse.maven:waystones-245755:${prop("deps.waystones.version")}")
    }
    modImplementation("maven.modrinth:balm:${prop("deps.balm.version")}")
    ifProp("deps.shogi.version") {
        modImplementation("curse.maven:shogi-1475746:$it")
    }

    modImplementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl.version")}") {
        exclude(group = "thedarkcolour", module = "kotlinforforge-neoforge")
    }

    fun xaeroImplementation(type: String, group: String? = null) {
        val parts = prop("deps.xaeros_$type.version").split('-').reversed()
        val version = parts[0]
        val mc = parts.getOrNull(1)

        val cleanType = type.filter { it != '_' }
        modImplementation("xaero.${group ?: cleanType}:xaero$cleanType-$loaderName-${mc ?: mcVersion}:$version")
    }

    xaeroImplementation("lib")
    xaeroImplementation("minimap")
    xaeroImplementation("world_map", "map")
}

sourceSets {
    named("main") {
        resources.setSrcDirs(listOf(layout.buildDirectory.dir("generated/stonecutter/main/resources")))
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(prop("deps.java.version").toInt())
    }
}

kotlin {
    jvmToolchain(prop("deps.java.version").toInt())
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
