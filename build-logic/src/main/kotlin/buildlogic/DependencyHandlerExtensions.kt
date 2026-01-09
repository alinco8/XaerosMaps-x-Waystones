package buildlogic

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate

fun Project.modImplementation(
    dependencyNotation: Any,
) {
    val loaderName: String by project

    dependencies {
        add(
            when (loaderName) {
                "fabric", "forge" -> "modImplementation"
                "neoforge" -> "implementation"
                else -> error("Unknown loader: $loaderName")
            },
            dependencyNotation
        )
    }
}