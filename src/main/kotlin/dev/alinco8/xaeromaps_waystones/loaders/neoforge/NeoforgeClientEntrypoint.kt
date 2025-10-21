//? if neoforge {
/*package dev.alinco8.xaeromaps_waystones.loaders.neoforge

import com.google.common.eventbus.Subscribe
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.config.ConfigScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge

@Mod(XaerosMapsWaystonesMod.MOD_ID, dist = [Dist.CLIENT])
class NeoforgeClientEntrypoint(bus: IEventBus, modContainer: ModContainer) {
    init {
        NeoForge.EVENT_BUS.register(this)

        modContainer.registerExtensionPoint<IConfigScreenFactory>(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, parent -> ConfigScreen.getConfigScreen(parent) }
        )
    }

    @SubscribeEvent
    fun onLeaveWorld(e: ClientPlayerNetworkEvent.LoggingOut) {
        XaerosMapsWaystonesMod.onLeaveWorld()
    }
}
*///?}
