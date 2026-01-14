package dev.alinco8.xmxw.config.yacl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.gui.controllers.string.IStringController

class HighlightedStringControllerBuilder(
    val opt: Option<String>,
) : ControllerBuilder<String> {
    private var highlightMap: Map<String, Int>? = null

    companion object {
        fun create(opt: Option<String>) =
            HighlightedStringControllerBuilder(opt)
    }

    fun highlightMap(map: Map<String, Int>): HighlightedStringControllerBuilder {
        highlightMap = map

        return this
    }

    override fun build(): Controller<String> {
        if (highlightMap.isNullOrEmpty()) error("Highlight map must be provided")

        return HighlightedStringController(opt, highlightMap!!)
    }
}
