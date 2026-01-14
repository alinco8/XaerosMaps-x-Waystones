@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://maven.kikugie.dev/snapshots") // Fletching Table
    maven("https://maven.kikugie.dev/releases") // Stonecutter
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.3.0")
    implementation("me.modmuss50:mod-publish-plugin:1.1.0")
    implementation("dev.kikugie:fletching-table:0.1.0-alpha.22")
    implementation("dev.kikugie:stonecutter:0.8.1")
}
