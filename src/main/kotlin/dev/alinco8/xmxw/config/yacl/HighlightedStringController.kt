package dev.alinco8.xmxw.config.yacl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.controllers.string.IStringController
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class HighlightedStringController(
    private val opt: Option<String>,
    private val highlightMap: Map<String, Int>,
) : IStringController<String> {
    override fun getString() = opt.pendingValue()

    override fun setFromString(value: String) {
        opt.requestSet(value)
    }

    override fun formatValue(): Component {
        val text = getString()
        val components = Component.empty()

        val stack = StringBuilder()
        var currentIndex = 0
        while (currentIndex < text.length) {
            val c = text[currentIndex]
            stack.append(c)

            when {
                stack.startsWith('{') && stack.endsWith('}')
                        && highlightMap.containsKey(
                    stack.toString().trimStart('{').trimEnd('}')
                ) -> {
                    components.append(
                        Component.literal(stack.toString())
                            .withStyle(
                                Style.EMPTY.withColor(
                                    highlightMap[stack.toString().trimStart('{').trimEnd('}')]!!
                                )
                            )
                    )
                    stack.clear()
                }
            }
            currentIndex++
        }
        if (stack.isNotEmpty()) {
            components.append(Component.literal(stack.toString()))
        }

        return Component.empty().append(components)
    }

    override fun option(): Option<String> {
        return opt
    }
}
