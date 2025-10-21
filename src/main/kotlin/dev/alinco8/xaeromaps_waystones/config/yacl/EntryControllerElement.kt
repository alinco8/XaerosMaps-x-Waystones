//? if yacl {
package dev.alinco8.xaeromaps_waystones.config.yacl

import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.ControllerWidget
import dev.isxander.yacl3.gui.controllers.TickBoxController
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput

class EntryControllerElement<T : Any>(
    control: EntryController<T>,
    screen: YACLScreen,
    dim: Dimension<Int>,
    private val keyWidget: AbstractWidget,
    private val valueWidget: AbstractWidget
) : ControllerWidget<EntryController<T>>(control, screen, dim) {
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return keyWidget.mouseClicked(mouseX, mouseY, button) || valueWidget.mouseClicked(
            mouseX,
            mouseY, button
        )
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return keyWidget.keyPressed(keyCode, scanCode, modifiers) || valueWidget.keyPressed(
            keyCode,
            scanCode, modifiers
        )
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        keyWidget.render(graphics, mouseX, mouseY, delta)
        valueWidget.render(graphics, mouseX, mouseY, delta)
    }

    override fun setDimension(dim: Dimension<Int>) {
        super.setDimension(dim)
        if (valueWidget is TickBoxController.TickBoxControllerElement) {
            this.keyWidget.dimension = dim.withWidth(dim.width()!! - 20)
            this.valueWidget.dimension = dim.withWidth(20).moved(dim.width()!! - 20, 0)
        } else {
            this.keyWidget.dimension = dim.withWidth(dim.width()!! / 2)
            this.valueWidget.dimension =
                dim.withWidth(dim.width()!! / 2).moved(dim.width()!! / 2, 0)
        }
    }

    override fun setFocused(focused: Boolean) {

    }

    override fun isFocused(): Boolean {
        return false
    }

    override fun getHoveredControlWidth() = unhoveredControlWidth
    override fun narrationPriority(): NarratableEntry.NarrationPriority {
        return NarratableEntry.NarrationPriority.NONE
    }

    override fun updateNarration(p0: NarrationElementOutput) {

    }
}
//?}