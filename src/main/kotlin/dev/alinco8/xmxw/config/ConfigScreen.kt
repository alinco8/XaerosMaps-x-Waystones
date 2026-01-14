//? if yacl {
package dev.alinco8.xmxw.config

import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.yacl.HighlightedStringControllerBuilder
import dev.alinco8.xmxw.config.yacl.configScreen
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Style
import xaero.hud.minimap.waypoint.WaypointColor

private fun <T : Any> Option.Builder<T>.bind(
    default: KProperty0<T>,
    current: KMutableProperty0<T>,
) = binding(default.get(), { current.get() }, { current.set(it) })

object ConfigScreen {
    fun getConfigScreen(parent: Screen): Screen {
        val h = XMXWConfig.HANDLER
        val d = h.defaults()
        val i = h.instance()

        return configScreen("xmxw") {
            builder.title(t("title"))
            builder.save(h::save)

            category("general") {
                builder.name(t("name"))

                group("waypoint") {
                    name(t("name"))

                    option("title") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointTitle, i::waypointTitle)
                        builder.controller(StringControllerBuilder::create)
                    }

                    option("nameFormat") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointNameFormat, i::waypointNameFormat)
                        builder.controller { opt ->
                            HighlightedStringControllerBuilder.create(opt)
                                .highlightMap(
                                    XMXWClient.replacers.asSequence().associate {
                                        it.key to ChatFormatting.LIGHT_PURPLE.color!!
                                    }
                                )
                        }
                    }

                    option("color") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointColor, i::waypointColor)
                        builder.controller { opt ->
                            EnumControllerBuilder.create(opt)
                                .enumClass(WaypointColor::class.java)
                                .formatValue { color ->
                                    color.getName().copy()
                                        .withStyle(Style.EMPTY.withColor(color.hex))
                                }
                        }
                    }

                    option("offsetX") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetX, i::waypointOffsetX)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                    option("offsetY") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetY, i::waypointOffsetY)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                    option("offsetZ") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetZ, i::waypointOffsetZ)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                }
            }
        }.generateScreen(parent)
    }
}
//? }
