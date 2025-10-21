//? if yacl {
package dev.alinco8.xaeromaps_waystones.config

import dev.alinco8.xaeromaps_waystones.config.yacl.EntryController
import dev.alinco8.xaeromaps_waystones.config.yacl.LengthRangeSpecifiedStringController
import dev.alinco8.xaeromaps_waystones.config.yacl.WaypointNameTemplateStringController
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import xaero.hud.minimap.waypoint.WaypointColor
import java.util.*

class ConfigScreen {
    companion object {
        fun getConfigScreen(parent: Screen): Screen {
            val instance = Config.INSTANCE.instance()
            val defaults = Config.INSTANCE.defaults()

            return YetAnotherConfigLib.createBuilder()
                .save { Config.INSTANCE.save() }
                .title(Component.translatable("xaeromaps_waystones.config.title"))
                .category(
                    ConfigCategory.createBuilder()
                        .name(Component.translatable("xaeromaps_waystones.config.category.general.name"))
                        .tooltip(Component.translatable("xaeromaps_waystones.config.category.general.tooltip"))
                        .option(
                            Option.createBuilder<String>()
                                .name(Component.translatable("xaeromaps_waystones.config.option.waypoint_icon.name"))
                                .description(OptionDescription.of(Component.translatable("xaeromaps_waystones.config.option.waypoint_icon.description")))
                                .binding(
                                    defaults.waypointIcon,
                                    { instance.waypointIcon },
                                    { value -> instance.waypointIcon = value }
                                )
                                .customController(
                                    LengthRangeSpecifiedStringController.create(
                                        1,
                                        2
                                    )
                                )
                                .build()
                        )
                        .option(
                            Option.createBuilder<String>()
                                .name(Component.translatable("xaeromaps_waystones.config.option.waypoint_name_template.name"))
                                .description(
                                    OptionDescription.of(
                                        Component.translatable(
                                            "xaeromaps_waystones.config.option.waypoint_name_template.description"
                                        )
                                    )
                                )
                                .binding(
                                    defaults.waypointNameTemplate,
                                    { instance.waypointNameTemplate },
                                    { value -> instance.waypointNameTemplate = value }
                                )
                                .customController(::WaypointNameTemplateStringController)
                                .build()
                        )
                        .option(
                            Option.createBuilder<WaypointColor>()
                                .name(Component.translatable("xaeromaps_waystones.config.option.waypoint_color_fallback.name"))
                                .description(
                                    OptionDescription.of(
                                        Component.translatable(
                                            "xaeromaps_waystones.config.option.waypoint_color_fallback.description"
                                        )
                                    )
                                )
                                .binding(
                                    defaults.waypointColorFallback,
                                    { instance.waypointColorFallback },
                                    { value -> instance.waypointColorFallback = value }
                                )
                                .controller(EnumDropdownControllerBuilder<WaypointColor>::create)
                                .build()
                        )
                        .group(
                            ListOption.createBuilder<MutableMap.MutableEntry<String, WaypointColor>>()
                                .name(Component.translatable("xaeromaps_waystones.config.group.waypoint_colors.name"))
                                .description(
                                    OptionDescription.of(
                                        Component.translatable(
                                            "xaeromaps_waystones.config.group.waypoint_colors.description"
                                        )
                                    )
                                )
                                .binding(
                                    defaults.waypointColors.entries.toList(),
                                    { instance.waypointColors.entries.toList() },
                                    { value ->
                                        instance.waypointColors =
                                            value.associate { it.toPair() }.toMutableMap()
                                    }
                                )
                                .customController { o ->
                                    EntryController(
                                        o,
                                        StringControllerBuilder::create,
                                        EnumDropdownControllerBuilder<WaypointColor>::create,
                                    )
                                }
                                .initial(
                                    AbstractMap.SimpleEntry(
                                        "waystones:waystone",
                                        defaults.waypointColorFallback,
                                    )
                                )
                                .build()
                        )
                        .build()
                )
                .build()
                .generateScreen(parent)
        }
    }
}
//?}