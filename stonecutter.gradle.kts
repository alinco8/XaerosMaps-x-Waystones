import buildlogic.prop

plugins {
    id("dev.kikugie.stonecutter")
    id("com.github.ben-manes.versions") version "0.53.0"
    id("project.base")
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    val (version, loader) = current.project.split("-", limit = 2)
    properties.tags(version, loader)

    constants.match(
        loader, "fabric", "neoforge", "forge"
    )

    dependencies["xaeros-minimap"] =
        node.project.prop("deps.xaeros_minimap.version")
    dependencies["waystones"] =
        node.project.prop("deps.waystones.version").split('+')[0]
    dependencies["balm"] =
        node.project.prop("deps.balm.version").split('+')[0]
}

stonecutter handlers {
    inherit("cfg", "toml")
}

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}

