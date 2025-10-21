package dev.alinco8.xaeromaps_waystones

import dev.alinco8.xaeromaps_waystones.config.Config
import dev.alinco8.xaeromaps_waystones.network.NetworkUtils
import dev.alinco8.xaeromaps_waystones.network.WaystoneActivatedPacket
import kotlin.jvm.java
import net.blay09.mods.balm.api.Balm
import net.minecraft.server.level.ServerPlayer
import org.slf4j.LoggerFactory

//? if waystones: >= 15 {
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent
import dev.alinco8.xaeromaps_waystones.network.WaystoneRemovedPacket
import net.blay09.mods.waystones.api.Waystone
import org.slf4j.Logger

//?} else {
/*import net.blay09.mods.waystones.api.IWaystone
import net.blay09.mods.waystones.api.WaystoneActivatedEvent
import net.blay09.mods.waystones.api.WaystoneUpdateReceivedEvent
import net.blay09.mods.waystones.api.KnownWaystonesEvent as WaystonesListReceivedEvent

*///?}

object XaerosMapsWaystonesMod {
    const val MOD_ID = "xaeromaps_waystones"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun initialize() {
        Config.INSTANCE.load()

        val events = Balm.getEvents()

        events.onEvent(
            WaystoneActivatedEvent::class.java,
            ::onWaystoneActivated
        )
        events.onEvent(
            WaystoneUpdateReceivedEvent::class.java,
            ::onWaystoneUpdateReceived
        )
        events.onEvent(WaystonesListReceivedEvent::class.java, ::onWaystonesListReceived)
        //? if waystones: >= 15 {
        events.onEvent(WaystoneRemovedEvent::class.java, ::onWaystoneRemoved)
        //?}
    }

    fun onLeaveWorld() {
        ManagerWatcher.stopWatching()
    }

    fun onWaystoneActivated(e: WaystoneActivatedEvent) {
        if (e.waystone.nameString().isEmpty() || e.player.isLocalPlayer) {
            return
        }
        LOGGER.debug("Activated waystone: {} at {}", e.waystone.nameString(), e.waystone.pos)

        val waystone = e.waystone
        val packet = WaystoneActivatedPacket(
            waystone.pos,
            waystone.nameString(),
            waystone.waystoneType.toString()
        )

        val player = e.player
        if (player is ServerPlayer) {
            NetworkUtils.sendToPlayer(player, packet)
        }
    }

    fun onWaystoneUpdateReceived(e: WaystoneUpdateReceivedEvent) {
        if (e.waystone.nameString().isEmpty()) {
            return
        }
        LOGGER.debug("Updated waystone: {} at {}", e.waystone.nameString(), e.waystone.pos)

        WaystonePointHelper.createOrUpdateWaystonePoint(
            e.waystone
        )
    }

    //? if waystones: >= 15 {
    fun onWaystoneRemoved(e: WaystoneRemovedEvent) {
        if (e.waystone.nameString().isEmpty()) {
            return
        }
        LOGGER.debug("Removed waystone: {} at {}", e.waystone.nameString(), e.waystone.pos)

        val waystone = e.waystone
        val packet = WaystoneRemovedPacket(
            waystone.pos,
            waystone.nameString(),
            e.waystone.waystoneType.toString()
        )

        NetworkUtils.sendToAllPlayers(packet)
    }
    //?}

    fun onWaystonesListReceived(e: WaystonesListReceivedEvent) {
        for (waystone in e.waystones) {
            if (waystone.nameString().isEmpty()) {
                continue
            }
            LOGGER.debug("Loaded waystone: {} at {}", waystone.nameString(), waystone.pos)

            WaystonePointHelper.createOrUpdateWaystonePoint(
                waystone
            )
        }
    }
}

//? if waystones: >= 15 {
fun Waystone.nameString(): String = this.name.string
//?} else {
/*fun IWaystone.nameString(): String = this.name
*///?}