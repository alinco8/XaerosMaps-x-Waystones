//? if yacl {
package dev.alinco8.xaeromaps_waystones.config.yacl

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.network.chat.Component

class EntryController<T : Any>(
    private val option: Option<MutableMap.MutableEntry<String, T>>,
    key: (Option<String>) -> ControllerBuilder<String>,
    value: (Option<T>) -> ControllerBuilder<T>
) : Controller<MutableMap.MutableEntry<String, T>> {
    private val keyController: Controller<String> =
        MapEntryOption(option, key).controller()
    private val valueController: Controller<T> =
        MapValueOption(option, value).controller()

    override fun option() = option

    override fun formatValue(): Component = Component.literal(option.pendingValue().toString())

    override fun provideWidget(
        screen: YACLScreen,
        widgetDimension: Dimension<Int>
    ): AbstractWidget {
        return EntryControllerElement(
            this, screen, widgetDimension,
            keyController.provideWidget(screen, widgetDimension),
            valueController.provideWidget(screen, widgetDimension)
        )
    }
}
//?}