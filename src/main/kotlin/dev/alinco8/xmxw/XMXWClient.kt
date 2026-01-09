package dev.alinco8.xmxw

import dev.alinco8.xmxw.config.XMXWConfig
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceKey
import xaero.common.minimap.waypoints.Waypoint
import xaero.hud.minimap.BuiltInHudModules
import xaero.hud.minimap.waypoint.WaypointColor

//? if >=1.21.10 {
/*import net.minecraft.resources.Identifier as ResourceLocation

*///?} else {
import net.minecraft.resources.ResourceLocation
import net.blay09.mods.balm.api.Balm

//? }

object XMXWClient {
    const val MOD_ID = "xmxw"
    fun loc(path: String): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

    fun initialize() {
        //? if >=1.21.10 {
        /*WaystonesListReceivedEvent.EVENT.register(::onWaystonesListReceived)

        *///? } else {
        Balm.getEvents().apply {
            onEvent(WaystonesListReceivedEvent::class.java, ::onWaystonesListReceived)
        }

        //? }
    }

    val waystones = mutableMapOf<ResourceLocation, MutableList<WaystoneData>>()
    val waypoints: Int2ObjectMap<Waypoint>?
        get() = BuiltInHudModules.MINIMAP?.currentSession
            ?.worldManager?.getCustomWaypoints(loc("waypoints"))

    data class WaystoneData(
        val x: Int,
        val y: Int,
        val z: Int,
        val name: String,
        val type: ResourceLocation,
    )

    fun onWaystonesListReceived(event: WaystonesListReceivedEvent) {
        waystones.values.forEach { waystones ->
            waystones.removeIf {
                it.type == event.waystoneType
            }
        }
        waystones.values.removeIf { it.isEmpty() }

        for (waystone in event.waystones) {
            if (!waystone.isValid || event.waystoneType.toString() != "waystones:waystone") continue

            val dimKey = waystone.dimension.loc()
            val waystoneData = WaystoneData(
                waystone.pos.x,
                waystone.pos.y,
                waystone.pos.z,
                waystone.name.string,
                event.waystoneType,
            )
            waystones.computeIfAbsent(dimKey) { mutableListOf() }.add(waystoneData)
        }

        updateWaystoneWaypoints(Minecraft.getInstance().level?.dimension()?.loc() ?: return)
    }

    fun onDimensionChange(dimKey: ResourceLocation) {
        updateWaystoneWaypoints(dimKey)
    }

    fun updateWaystoneWaypoints(dimKey: ResourceLocation) {
        waypoints?.let { waypoints ->
            waypoints.clear()

            val config = XMXWConfig.HANDLER.instance()
            waystones[dimKey]?.forEachIndexed { index, waypoint ->
                waypoints[index] = Waypoint(
                    waypoint.x,
                    waypoint.y,
                    waypoint.z,
                    config.waypointNameFormat.replace("{name}", waypoint.name),
                    config.waypointTitle,
                    config.waypointColor,
                )
            }
        }
    }
}

//? if >=1.21.10 {
/*fun <T : Any> ResourceKey<T>.loc() = this.identifier()
*///? } else {
fun <T : Any> ResourceKey<T>.loc() = this.location()
//? }