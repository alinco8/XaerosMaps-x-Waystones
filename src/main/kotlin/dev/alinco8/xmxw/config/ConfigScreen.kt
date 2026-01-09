package dev.alinco8.xmxw.config

import dev.alinco8.xmxw.XMXWClient
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import net.minecraft.client.gui.screens.Screen
import xaero.hud.minimap.waypoint.WaypointColor

object ConfigScreen {
    fun getConfigScreen(parent: Screen): Screen = YetAnotherConfigLib(XMXWClient.MOD_ID) {
        val default = XMXWConfig.HANDLER.defaults()
        val instance = XMXWConfig.HANDLER.instance()

        save {
            XMXWConfig.HANDLER.save()
        }

        categories.register("general") {
            groups.register("waypoint") {
                options.register("waypointTitle") {
                    binding(
                        default.waypointTitle,
                        { instance.waypointTitle },
                        { instance.waypointTitle = it }
                    )
                    controller(StringControllerBuilder::create)
                }
                options.register("waypointNameFormat") {
                    binding(
                        default.waypointNameFormat,
                        { instance.waypointNameFormat },
                        { instance.waypointNameFormat = it }
                    )
                    controller(StringControllerBuilder::create)
                }
                options.register("waypointColor") {
                    binding(
                        default.waypointColor,
                        { instance.waypointColor },
                        { instance.waypointColor = it }
                    )
                    controller { opt ->
                        EnumControllerBuilder.create(opt)
                            .enumClass(WaypointColor::class.java)
                            .formatValue { color ->
                                color.getName().copy().withColor(color.hex)
                            }
                    }
                }
            }
        }
    }.generateScreen(parent)
}