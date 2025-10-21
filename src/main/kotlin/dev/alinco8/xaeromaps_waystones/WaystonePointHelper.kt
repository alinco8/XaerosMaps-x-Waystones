package dev.alinco8.xaeromaps_waystones

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod.LOGGER
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import xaero.common.minimap.waypoints.Waypoint
import xaero.hud.minimap.waypoint.WaypointColor
import xaero.hud.minimap.waypoint.WaypointPurpose
import xaero.hud.minimap.waypoint.set.WaypointSet
import xaero.hud.minimap.world.MinimapWorldManager
import dev.alinco8.xaeromaps_waystones.config.Config

//? if waystones: > 15 {
import net.blay09.mods.waystones.api.Waystone

//?} else {
/*import net.blay09.mods.waystones.api.IWaystone as Waystone

*///?}

class WaystonePointHelper {
    companion object {
        fun createOrUpdateWaystonePoint(
            waystone: Waystone,
        ) {
            val instance = Config.INSTANCE.instance()

            createOrUpdateWaystonePoint(
                instance.waypointNameTemplate.replace(
                    "{waystone_name}",
                    waystone.nameString(),
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
            ManagerWatcher.execute(dimKey) { waypointSet ->
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
        }

        fun removeWaypoint(
            x: Int,
            y: Int,
            z: Int,
            dimKey: ResourceKey<Level>? = null,
        ) {
            ManagerWatcher.execute(dimKey) { waypointSet ->
                waypointSet.remove(
                    waypointSet.waypoints.find {
                        it.isTemporary && it.x == x && it.y == y && it.z == z
                    }
                )
            }
        }

        fun waypointColorFromWaystone(loc: String): WaypointColor {
            val instance = Config.INSTANCE.instance()

            return instance.waypointColors[loc] ?: instance.waypointColorFallback
        }
    }
}