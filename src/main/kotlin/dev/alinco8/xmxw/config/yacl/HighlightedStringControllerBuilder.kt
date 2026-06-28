package dev.alinco8.xmxw.config.yacl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import net.minecraft.ChatFormatting

class HighlightedStringControllerBuilder(
    val opt: Option<String>,
) : ControllerBuilder<String> {
    private var highlightMap: Map<String, ChatFormatting>? = null

    companion object {
        fun create(opt: Option<String>) =
            HighlightedStringControllerBuilder(opt)
    }

    fun highlightMap(map: Map<String, ChatFormatting>): HighlightedStringControllerBuilder {
        highlightMap = map

        return this
    }

    @Suppress("UnstableApiUsage")
    override fun build(): Controller<String> {
        if (highlightMap.isNullOrEmpty()) error("Highlight map must be provided")

        return HighlightedStringController(opt, highlightMap!!)
    }
}
