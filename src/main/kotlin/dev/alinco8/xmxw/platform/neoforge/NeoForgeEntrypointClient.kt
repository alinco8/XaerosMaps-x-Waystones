//? if neoforge {
package dev.alinco8.xmxw.platform.neoforge

import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.ConfigScreen
import dev.alinco8.xmxw.loc
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent

@Mod(XMXWClient.MOD_ID, dist = [Dist.CLIENT])
class NeoForgeEntrypointClient(modContainer: ModContainer) {
    init {
        XMXWClient.initialize()

        NeoForge.EVENT_BUS.register(this)
        modContainer.registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { _, parent ->
                ConfigScreen.getConfigScreen(parent)
            }
        }
    }

    @SubscribeEvent
    fun onDimensionChange(event: PlayerEvent.PlayerChangedDimensionEvent) {
        XMXWClient.onDimensionChange(
            event.to.loc()
        )
    }
    @SubscribeEvent
    fun onClientWorldChange(event: PlayerEvent.PlayerLoggedInEvent) {
        XMXWClient.onDimensionChange(
            event.entity.level().dimension().loc()
        )
    }
}
//? }