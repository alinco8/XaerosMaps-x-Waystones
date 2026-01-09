import buildlogic.prop
import buildlogic.propOrNull

plugins {
    id("dev.kikugie.stonecutter")
    id("project.base")
    id("com.github.ben-manes.versions") version "0.53.0"
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    constants.match(
        node.metadata.project.substringAfter("-"),
        "fabric", "neoforge"
    )
    constants["yacl"] = node.project.propOrNull("deps.yacl") != null

    dependencies["xaeros-minimap"] =
        node.project.prop("deps.xaeros_minimap")
    dependencies["waystones"] =
        node.project.prop("deps.waystones").split('+')[0]
    dependencies["balm"] =
        node.project.prop("deps.balm").split('+')[0]
}

stonecutter handlers {
    inherit("cfg", "toml")
}

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}