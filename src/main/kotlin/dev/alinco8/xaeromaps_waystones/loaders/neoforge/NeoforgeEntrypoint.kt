//? if neoforge {
/*package dev.alinco8.xaeromaps_waystones.loaders.neoforge

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.network.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.network.WaystoneRemovedPacket
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@Mod(XaerosMapsWaystonesMod.MOD_ID)
class NeoforgeEntrypoint(bus: IEventBus) {
    init {
        bus.register(this)

        XaerosMapsWaystonesMod.initialize()
    }

    @SubscribeEvent
    fun registerPayloadHandlers(e: RegisterPayloadHandlersEvent) {
        val registrar = e.registrar("1")

        registrar.playToClient(
            WaystoneActivatedPacket.TYPE,
            WaystoneActivatedPacket.STREAM_CODEC
        ) { packet, _ -> WaystoneActivatedPacket.handle(packet) }
        registrar.playToClient(
            WaystoneRemovedPacket.TYPE,
            WaystoneRemovedPacket.STREAM_CODEC
        ) { packet, _ -> WaystoneRemovedPacket.handle(packet) }
    }
}
*///?}
