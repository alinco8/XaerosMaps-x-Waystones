//? if neoforge {
/*package dev.alinco8.xaeromaps_waystones.loaders.neoforge;

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.config.ConfigScreen
import dev.alinco8.xaeromaps_waystones.packets.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.packets.WaystoneRemovedPacket
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@Mod(XaerosMapsWaystonesMod.MOD_ID)
class NeoforgeEntrypoint(bus: IEventBus, modContainer: ModContainer) {
    init {
        bus.register(this)
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(ClientEvents())
        }

        modContainer.registerExtensionPoint<IConfigScreenFactory>(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, parent -> ConfigScreen.getConfigScreen(parent) }
        )

        XaerosMapsWaystonesMod.initialize()
    }

    class ClientEvents {
        @SubscribeEvent
        fun onClientTick(e: ClientTickEvent.Post) {
            XaerosMapsWaystonesMod.onClientTickPost()
        }
    }

    @SubscribeEvent
    fun registerPayloadHandlers(e: RegisterPayloadHandlersEvent) {
        val registrar = e.registrar("1")

        registrar.playToClient(
            WaystoneActivatedPacket.TYPE,
            WaystoneActivatedPacket.STREAM_CODEC,
            WaystoneActivatedPacket::handle
        )
        registrar.playToClient(
            WaystoneRemovedPacket.TYPE,
            WaystoneRemovedPacket.STREAM_CODEC,
            WaystoneRemovedPacket::handle
        )
    }
}
*///?}
