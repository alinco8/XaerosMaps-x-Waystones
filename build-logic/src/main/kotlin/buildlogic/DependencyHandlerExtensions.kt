package buildlogic

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate

fun Project.modImplementation(
    dependencyNotation: Any,
    configurationAction: Action<ExternalModuleDependency>? = null
) {
    val loaderName: String by project
    val configuration = when (loaderName) {
        "fabric", "forge" -> "modImplementation"
        "neoforge" -> "implementation"
        else -> error("Unknown loader: $loaderName")
    }

    dependencies {
        if (configurationAction != null) {
            addDependencyTo(
                this,
                configuration,
                dependencyNotation,
                configurationAction,
            )
        } else {
            add(
                configuration,
                dependencyNotation,
            )
        }
    }
}
