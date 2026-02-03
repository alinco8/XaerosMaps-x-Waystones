package dev.alinco8.xmxw

//? if <=1.20.1 {
/*import net.blay09.mods.waystones.api.KnownWaystonesEvent as WaystonesListReceivedEvent
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent

*///? } else {
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent

//? }

//? if >=1.21.10 {
/*import net.minecraft.resources.Identifier as ResourceLocation

*///?} else {
import net.minecraft.resources.ResourceLocation
import net.blay09.mods.balm.api.Balm

//? }

import dev.alinco8.xmxw.config.XMXWConfig
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import java.util.UUID
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import xaero.common.minimap.waypoints.Waypoint
import xaero.hud.minimap.BuiltInHudModules
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object XMXWClient {
    const val MOD_ID = "xmxw"
    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    val replacers = mapOf<String, (WaystoneData) -> String>(
        "name" to { it.name },
    )

    @JvmStatic
    fun loc(path: String): ResourceLocation {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        //? } else {
        /*return ResourceLocation(MOD_ID, path)
        *///? }
    }

    @JvmStatic
    fun displayMessage(vararg component: Component) {
        val message = Component.empty()
            .append(Component.literal("[XMXW] ").withStyle(ChatFormatting.GREEN))
        for (part in component) {
            message.append(part)
        }

        Minecraft.getInstance().player?.displayClientMessage(message, false)
    }

    fun initialize() {
        LOGGER.debug("Initializing XMXW Client")
        XMXWConfig.HANDLER.load()

        //? if >=1.21.10 {
        /*WaystonesListReceivedEvent.EVENT.register(::onWaystonesListReceived)

        *///? } else {
        Balm.getEvents().apply {
            onEvent(WaystonesListReceivedEvent::class.java, ::onWaystonesListReceived)
            onEvent(WaystoneUpdateReceivedEvent::class.java, ::onWaystoneUpdateReceived)
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
        val id: UUID,
    )

    fun onWaystonesListReceived(event: WaystonesListReceivedEvent) {
        //? if <=1.20.1 {
        /*val receivedWaystones = event.waystones.groupBy { it.waystoneType }
        *///? } else {
        val receivedWaystones = mapOf(event.waystoneType to event.waystones)
        //? }

        LOGGER.debug(
            "Received waystones list with {} waystone types and total of {} waystones",
            receivedWaystones.size,
            receivedWaystones.values.sumOf { it.size },
        )

        if (receivedWaystones.isEmpty()) {
            waystones.clear()

            updateWaystoneWaypoints(Minecraft.getInstance().level?.dimension()?.loc() ?: return)
        }

        for ((waystoneType, receivedWaystones) in receivedWaystones) {
            LOGGER.debug("Processing {} waystones of type {}", receivedWaystones.size, waystoneType)
            if (waystoneType.toString() != "waystones:waystone") continue

            waystones.values.forEach { waystones ->
                waystones.removeIf {
                    it.type == waystoneType
                }
            }
            waystones.values.removeIf { it.isEmpty() }

            for (waystone in receivedWaystones) {
                LOGGER.debug("Processing waystone {} at {}", waystone.name, waystone.pos)
                if (!waystone.isValid) continue

                val dimKey = waystone.dimension.loc()
                val waystoneData = WaystoneData(
                    waystone.pos.x,
                    waystone.pos.y,
                    waystone.pos.z,
                    //? if <=1.20.1 {
                    /*waystone.name,
                    *///? } else {
                    waystone.name.string,
                    //? }
                    waystoneType,
                    waystone.waystoneUid,
                )
                waystones.computeIfAbsent(dimKey) { mutableListOf() }.add(waystoneData)
            }

            updateWaystoneWaypoints(Minecraft.getInstance().level?.dimension()?.loc() ?: return)
        }
    }

    fun onWaystoneUpdateReceived(event: WaystoneUpdateReceivedEvent) {
        LOGGER.debug(
            "Received waystone update for waystone {} {}",
            event.waystone.name,
            event.waystone.waystoneUid,
        )
        if (!event.waystone.isValid || event.waystone.waystoneType.toString() != "waystones:waystone") return

        for (waystonesList in waystones.values) {
            waystonesList.removeIf {
                it.type == event.waystone.waystoneUid
            }
        }
        waystones.values.removeIf { it.isEmpty() }

        val waystone = event.waystone
        val waystoneData = WaystoneData(
            waystone.pos.x,
            waystone.pos.y,
            waystone.pos.z,
            //? if <=1.20.1 {
            /*waystone.name,
            *///? } else {
            waystone.name.string,
            //? }
            waystone.waystoneType,
            waystone.waystoneUid,
        )
        val dimKey = waystone.dimension.loc()
        waystones.computeIfAbsent(dimKey) { mutableListOf() }.add(waystoneData)
    }

    fun onDimensionChange(dimKey: ResourceLocation) {
        LOGGER.debug("Dimension changed to {}", dimKey)

        updateWaystoneWaypoints(dimKey)
    }

    fun updateWaystoneWaypoints(dimKey: ResourceLocation) {
        LOGGER.debug("Updating waypoints for dimension {}", dimKey)

        waypoints?.let { waypoints ->
            waypoints.clear()

            val config = XMXWConfig.HANDLER.instance()

            waystones[dimKey]?.forEachIndexed { index, waystone ->
                var waypointName = config.waypointNameFormat
                for ((key, replacer) in replacers.entries) {
                    waypointName = waypointName.replace("{$key}", replacer(waystone))
                }

                waypoints[index] = Waypoint(
                    waystone.x + config.waypointOffsetX,
                    waystone.y + config.waypointOffsetY,
                    waystone.z + config.waypointOffsetZ,
                    waypointName,
                    config.waypointTitle,
                    config.waypointColor,
                ).also {
                    it.visibility = config.waypointVisibility
                }
            }
        }
    }
}

//? if >=1.21.10 {
/*fun <T : Any> ResourceKey<T>.loc() = this.identifier()
*///? } else {
fun <T : Any> ResourceKey<T>.loc() = this.location()
//? }
