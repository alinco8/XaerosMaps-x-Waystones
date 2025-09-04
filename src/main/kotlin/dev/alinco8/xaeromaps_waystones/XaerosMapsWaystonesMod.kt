package dev.alinco8.xaeromaps_waystones;


import dev.alinco8.xaeromaps_waystones.config.Config
import dev.alinco8.xaeromaps_waystones.packets.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.packets.WaystoneRemovedPacket
import net.blay09.mods.balm.api.Balm
import net.blay09.mods.waystones.api.event.WaystoneActivatedEvent
import net.blay09.mods.waystones.api.event.WaystoneRemovedEvent
import net.blay09.mods.waystones.api.event.WaystoneUpdateReceivedEvent
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import xaero.hud.minimap.BuiltInHudModules

//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor

*///?} elif fabric {
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

//?}


object XaerosMapsWaystonesMod {
    const val MOD_ID = "xaeromaps_waystones"

    fun initialize() {
        Config.INSTANCE.load()

        val events = Balm.getEvents()

        events.onEvent(WaystoneActivatedEvent::class.java, ::onWaystoneActivated)
        events.onEvent(WaystoneUpdateReceivedEvent::class.java, ::onWaystoneUpdateReceived)
        events.onEvent(WaystoneRemovedEvent::class.java, ::onWaystoneRemoved)
        events.onEvent(WaystonesListReceivedEvent::class.java, ::onWaystonesListReceived)
    }

    fun onClientTickPost() {
        if (WaystonePointHelper.queue.isNotEmpty()
            && Minecraft.getInstance().player != null
            && BuiltInHudModules.MINIMAP
                .currentSession.worldManager.currentWorld != null
        ) {
            WaystonePointHelper.processQueue()
        }
    }

    fun onWaystoneActivated(e: WaystoneActivatedEvent) {
        if (e.waystone.name.string.isEmpty() || e.player.isLocalPlayer) {
            return
        }

        val waystone = e.waystone
        val packet = WaystoneActivatedPacket(
            waystone.pos,
            waystone.name.string,
            waystone.waystoneType.toString()
        )

        //? if neoforge {
        /*PacketDistributor.sendToPlayer(e.player as ServerPlayer, packet)
        *///?} else if fabric {
        if (e.player is ServerPlayer) {
            ServerPlayNetworking.send(
                e.player as ServerPlayer,
                packet
            )
        }
        //?}
    }

    fun onWaystoneUpdateReceived(e: WaystoneUpdateReceivedEvent) {
        if (e.waystone.name.string.isEmpty()) {
            return
        }

        WaystonePointHelper.createOrUpdateWaystonePoint(
            e.waystone
        )
    }

    fun onWaystoneRemoved(e: WaystoneRemovedEvent) {
        if (e.waystone.name.string.isEmpty()) {
            return
        }

        val waystone = e.waystone
        val packet = WaystoneRemovedPacket(waystone.pos, waystone.name.string)
        //? if neoforge {
        /*PacketDistributor.sendToAllPlayers(packet)
        *///?} else if fabric {
        val instance = Minecraft.getInstance()
        for (player in PlayerLookup.all(
            instance.singleplayerServer
                ?: return
        )) {
            ServerPlayNetworking.send(
                player,
                packet
            )
        }
        //?}
    }

    fun onWaystonesListReceived(e: WaystonesListReceivedEvent) {
        for (waystone in e.waystones) {
            if (waystone.name.string.isEmpty()) {
                continue
            }

            WaystonePointHelper.createOrUpdateWaystonePoint(
                waystone
            )
        }
    }
}