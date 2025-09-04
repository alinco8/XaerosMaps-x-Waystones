package dev.alinco8.xaeromaps_waystones

import dev.alinco8.xaeromaps_waystones.config.Config
import net.blay09.mods.waystones.api.Waystone
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import xaero.common.minimap.waypoints.Waypoint
import xaero.hud.minimap.BuiltInHudModules
import xaero.hud.minimap.waypoint.WaypointColor
import xaero.hud.minimap.waypoint.WaypointPurpose
import xaero.hud.minimap.waypoint.set.WaypointSet
import xaero.hud.minimap.world.MinimapWorldManager

class WaystonePointHelper {
    companion object {
        val worldManager: MinimapWorldManager
            get() = BuiltInHudModules.MINIMAP.currentSession.worldManager

        val queue = mutableListOf<WaypointQueueType>()

        fun getWorldWaypointSet(
            dimKey: ResourceKey<Level>? = null,
        ): WaypointSet? {
            return if (
                worldManager.currentWorld == null
            ) {
                null
            } else if (dimKey == null) {
                worldManager.currentWorld.currentWaypointSet
            } else {
                worldManager.currentRootContainer.allWorldsIterable.find { world ->
                    world.dimId == dimKey
                }?.currentWaypointSet
            }
        }

        fun processQueue() {
            if (queue.isEmpty()) {
                return
            }

            val copy = queue.toList()
            queue.clear()
            for (item in copy) {
                when (item) {
                    is WaypointQueueType.CreateOrUpdate -> {
                        createOrUpdateWaystonePoint(
                            item.name,
                            item.initials,
                            item.color,
                            item.x,
                            item.y,
                            item.z,
                            item.dimKey
                        )
                    }

                    is WaypointQueueType.Remove -> {
                        removeWaypoint(
                            item.x,
                            item.y,
                            item.z,
                            item.dimKey
                        )
                    }
                }
            }
        }

        fun createOrUpdateWaystonePoint(
            waystone: Waystone,
        ) {
            val instance = Config.INSTANCE.instance()

            createOrUpdateWaystonePoint(
                instance.waypointNameTemplate.replace(
                    "{waystone_name}",
                    waystone.name.string
                ),
                instance.waypointIcon,
                waypointColorFromWaystone(waystone.waystoneType.toString()),
                waystone.pos.x,
                waystone.pos.y,
                waystone.pos.z,
                waystone.dimension
            )
        }

        fun createOrUpdateWaystonePoint(
            name: String,
            initials: String,
            color: WaypointColor,
            x: Int,
            y: Int,
            z: Int,
            dimKey: ResourceKey<Level>?,
        ) {
            val waypointSet = getWorldWaypointSet(dimKey)
                ?: run {
                    queue.add(
                        WaypointQueueType.CreateOrUpdate(
                            name,
                            initials,
                            color,
                            x,
                            y,
                            z,
                            dimKey ?: worldManager.currentWorld.dimId
                        )
                    )
                    return
                }
            val waypoint = waypointSet.waypoints.find {
                it.isTemporary && it.x == x && it.y == y && it.z == z
            }

            if (waypoint == null) {
                val waypoint = Waypoint(
                    x,
                    y,
                    z,
                    name,
                    initials,
                    color,
                    WaypointPurpose.NORMAL,
                    true
                )

                waypointSet.add(
                    waypoint
                )
            } else {
                waypoint.name = name
                waypoint.initials = Config.INSTANCE.instance().waypointIcon
                waypoint.x = x
                waypoint.y = y
                waypoint.z = z
            }
        }

        fun removeWaypoint(
            x: Int,
            y: Int,
            z: Int,
            dimKey: ResourceKey<Level>? = null,
        ) {
            val waypointSet = getWorldWaypointSet(dimKey)
                ?: run {
                    queue.add(
                        WaypointQueueType.Remove(
                            x,
                            y,
                            z,
                            dimKey ?: worldManager.currentWorld.dimId
                        )
                    )
                    return
                }
            waypointSet.remove(
                waypointSet.waypoints.find {
                    it.isTemporary && it.x == x && it.y == y && it.z == z
                }
            )
        }

        fun waypointColorFromWaystone(loc: String): WaypointColor {
            val instance = Config.INSTANCE.instance()

            return instance.waypointColors[loc] ?: instance.waypointColorFallback
        }
    }

    sealed class WaypointQueueType {
        data class CreateOrUpdate(
            val name: String,
            val initials: String,
            val color: WaypointColor,
            val x: Int,
            val y: Int,
            val z: Int,
            val dimKey: ResourceKey<Level>,
        ) : WaypointQueueType()

        data class Remove(
            val x: Int,
            val y: Int,
            val z: Int,
            val dimKey: ResourceKey<Level>,
        ) : WaypointQueueType()
    }
}