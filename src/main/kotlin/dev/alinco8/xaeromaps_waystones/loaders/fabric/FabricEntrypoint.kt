//? if fabric {
package dev.alinco8.xaeromaps_waystones.loaders.fabric

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.packets.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.packets.WaystoneRemovedPacket
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

class FabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        ClientTickEvents.END_WORLD_TICK.register({ _ ->
            XaerosMapsWaystonesMod.onClientTickPost()
        })

        registerPayloads()

        XaerosMapsWaystonesMod.initialize()
    }

    fun registerPayloads() {
        PayloadTypeRegistry.playS2C().register(
            WaystoneActivatedPacket.TYPE,
            WaystoneActivatedPacket.STREAM_CODEC
        )
        PayloadTypeRegistry.playS2C().register(
            WaystoneRemovedPacket.TYPE,
            WaystoneRemovedPacket.STREAM_CODEC
        )

        ClientPlayNetworking.registerGlobalReceiver(
            WaystoneActivatedPacket.TYPE,
            WaystoneActivatedPacket::handle
        )
        ClientPlayNetworking.registerGlobalReceiver(
            WaystoneRemovedPacket.TYPE,
            WaystoneRemovedPacket::handle
        )
    }
}
//?}
