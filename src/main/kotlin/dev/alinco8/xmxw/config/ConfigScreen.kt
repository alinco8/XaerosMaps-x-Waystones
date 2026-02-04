package dev.alinco8.xmxw.config

//? if >=1.21.5 {
/*import xaero.hud.minimap.waypoint.WaypointVisibilityType
*///? } else {
import xaero.common.minimap.waypoints.WaypointVisibilityType
//? }

import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.yacl.HighlightedStringControllerBuilder
import dev.alinco8.xmxw.config.yacl.configScreen
import dev.alinco8.xmxw.loc
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Style
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
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
            builder.save {
                h.save()
                val player = Minecraft.getInstance().player ?: return@save
                XMXWClient.updateWaystoneWaypoints(player.level().dimension().loc())
            }

            category("waypoint") {
                builder.name(t("name"))

                group("offset") {
                    name(t("name"))

                    option("x") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetX, i::waypointOffsetX)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                    option("y") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetY, i::waypointOffsetY)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                    option("z") {
                        name(t("name"))
                        builder.description(OptionDescription.of(t("description")))
                        builder.bind(d::waypointOffsetZ, i::waypointOffsetZ)
                        builder.controller(IntegerFieldControllerBuilder::create)
                    }
                }

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
                        HighlightedStringControllerBuilder
                            .create(opt)
                            .highlightMap(
                                XMXWClient.replacers.asSequence().associate {
                                    it.key to ChatFormatting.LIGHT_PURPLE.color!!
                                },
                            )
                    }
                }

                option("visibility") {
                    name(t("name"))
                    builder.description(OptionDescription.of(t("description")))
                    builder.bind(d::waypointVisibility, i::waypointVisibility)
                    builder.controller { opt ->
                        EnumControllerBuilder
                            .create(opt)
                            .enumClass(WaypointVisibilityType::class.java)
                            .formatValue { vis ->
                                vis.translation
                            }
                    }
                }

                group("colorCandidates") {
                    name(t("name"))

                    builder.collapsed(true)

                    WaypointColor.entries.sortedBy { it.name }.forEach { color ->
                        builder.option(
                            Option.createBuilder<Boolean>()
                                .name(
                                    Component.literal(color.name)
                                        .withStyle(Style.EMPTY.withColor(color.hex))
                                )
                                .description(
                                    OptionDescription.of(
                                        i18n.option(
                                            categoryId,
                                            groupId,
                                            "_color",
                                            "description",
                                            color.name
                                        )
                                    )
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .binding(
                                    d.waypointColorCandidates[color] ?: false,
                                    { i.waypointColorCandidates[color] ?: false },
                                    { i.waypointColorCandidates[color] = it },
                                )
                                .build()
                        )
                    }
                }
            }
        }.generateScreen(parent)
    }
}
