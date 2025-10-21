//? if yacl {
package dev.alinco8.xaeromaps_waystones.config.yacl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.controllers.string.IStringController
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

data class WaypointNameTemplateStringController(
    private val option: Option<String>,
) : IStringController<String> {

    override fun getString() = option.pendingValue()
    override fun option() = option

    override fun setFromString(value: String) {
        option.requestSet(value)
    }

    override fun formatValue(): Component {
        val waystoneName = Component.literal("{waystone_name}")
            .setStyle(Style.EMPTY.withColor(0xFF00FF))

        // array component
        val parts = option.pendingValue().split("{waystone_name}")
        val component = Component.literal("")
        for (i in parts.indices) {
            component.append(Component.literal(parts[i]))
            if (i < parts.size - 1) {
                component.append(waystoneName)
            }
        }

        return component
    }
}
//?}