//? if fabric {
package dev.alinco8.xaeromaps_waystones.loaders.fabric

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.network.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.network.WaystoneRemovedPacket
import net.fabricmc.api.ClientModInitializer

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

class FabricClientEntrypoint : ClientModInitializer {

    override fun onInitializeClient() {
        // Network Handlers
        ClientPlayNetworking.registerGlobalReceiver(
            WaystoneActivatedPacket.TYPE
        ) { packet, _ -> WaystoneActivatedPacket.handle(packet) }

        ClientPlayNetworking.registerGlobalReceiver(
            WaystoneRemovedPacket.TYPE
        ) { packet, _ -> WaystoneRemovedPacket.handle(packet) }
    }
}
//?}
