package buildlogic

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.strictMaven(url: String, group: String) = exclusiveContent {
    forRepository { maven(url) }
    @Suppress("UnstableApiUsage")
    filter { includeGroupAndSubgroups(group) }
}
