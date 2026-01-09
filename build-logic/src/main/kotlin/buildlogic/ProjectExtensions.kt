package buildlogic

import org.gradle.api.Project

fun Project.propOrNull(key: String): String? =
    findProperty(key)?.toString() ?: if (System.getenv()
            .containsKey(key)
    ) System.getenv(key) else null

fun Project.prop(key: String): String = propOrNull(key) ?: error("Property '$key' not found")
fun Project.ifProp(key: String, block: Project.(String) -> Unit) {
    propOrNull(key)?.let { block(it) }
}

fun Project.getTemplateProps(loaderName: String): Map<String, Map<String, String>> {
    val deps = properties.filter { (k, v) ->
        k.startsWith("deps.") && k.endsWith(".range") && v is String
    }.map { (key, value) ->
        if (!(value as String).contains(',')) return@map key.removePrefix("deps.")
            .removeSuffix(".range") to value.trim()
        val (min, max) = value.split(',', limit = 2)
        val range = when (loaderName) {
            "fabric" -> ">=$min" + if (max.isNotBlank()) ",<$max" else ""
            "forge", "neoforge" -> "[$min,$max)"
            else -> error("Unsupported loader: $loaderName")
        }
        key.removePrefix("deps.").removeSuffix(".range") to range.trim()
    }.toMap()

    return mapOf(
        "mod" to mapOf(
            "id" to prop("mod.id"),
            "version" to prop("mod.version"),
            "name" to prop("mod.name"),
            "group" to prop("mod.group"),
            "authors" to prop("mod.authors"),
            "license" to prop("mod.license"),
            "description" to prop("mod.description"),
            "sources_url" to prop("mod.sources_url"),
            "homepage_url" to prop("mod.homepage_url"),
            "issues_url" to prop("mod.issues_url"),
            "forge_updates_url" to prop("mod.forge_updates_url"),
        ),
        "deps" to deps
    )
}