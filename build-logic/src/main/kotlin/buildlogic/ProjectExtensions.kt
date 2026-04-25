package buildlogic

import org.gradle.api.Project

fun Project.propOrNull(key: String): String? =
    findProperty(key)?.toString() ?: if (System.getenv()
            .containsKey(key)
    ) System.getenv(key) else null

fun Project.prop(key: String): String =
    propOrNull(key) ?: error("Property '$key' not found in project '${this.name}'")

fun Project.ifProp(key: String, block: Project.(String) -> Unit) {
    propOrNull(key)?.let { block(it) }
}

private fun unflatten(flat: Map<String, Any?>): Map<String, Any?> {
    val result = mutableMapOf<String, Any?>()

    for ((key, value) in flat) {
        val parts = key.split('.')
        var current = result

        for (i in parts.indices) {
            val part = parts[i]
            if (i == parts.size - 1) {
                current[part] = value
            } else {
                @Suppress("UNCHECKED_CAST")
                current =
                    current.getOrPut(part) {
                        mutableMapOf<String, Any>()
                    } as MutableMap<String, Any?>
            }
        }
    }

    return result
}

private fun Project.getPropertiesWith(
    start: String? = null,
    end: String? = null,
    trim: Boolean = true,
): Map<String, Any?> {
    var filtered = properties.filter { (k, _) ->
        (start == null || k.startsWith(start)) && (end == null || k.endsWith(end))
    }

    if (trim) {
        filtered = filtered.mapKeys { (k, _) ->
            var newKey = k
            if (start != null) newKey = newKey.removePrefix(start)
            if (end != null) newKey = newKey.removeSuffix(end)

            newKey
        }
    }

    return filtered.toMap()
}

fun Project.getTemplateProps(
    mcVersion: String,
    loaderName: String
): Map<String, Any?> {
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
        "env" to mapOf(
            "loader" to loaderName,
            "minecraft" to mcVersion,
        ),
        "mod" to unflatten(getPropertiesWith(start = "mod.")),
        "deps" to deps
    )
}
