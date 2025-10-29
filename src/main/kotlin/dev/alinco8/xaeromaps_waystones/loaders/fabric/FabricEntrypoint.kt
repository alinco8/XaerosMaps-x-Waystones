//? if fabric {
package dev.alinco8.xaeromaps_waystones.loaders.fabric

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.network.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.network.WaystoneRemovedPacket

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.server.MinecraftServer

class FabricEntrypoint : ModInitializer {
    companion object {
        var server: MinecraftServer? = null
    }

    override fun onInitialize() {
        // Events
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            FabricEntrypoint.server = server
        }

        // Network Packets
        PayloadTypeRegistry.playS2C().register(
            WaystoneActivatedPacket.TYPE,
            WaystoneActivatedPacket.STREAM_CODEC
        )
        PayloadTypeRegistry.playS2C().register(
            WaystoneRemovedPacket.TYPE,
            WaystoneRemovedPacket.STREAM_CODEC
        )

        // Other
        XaerosMapsWaystonesMod.initialize()
    }
}
//?}
