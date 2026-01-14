//? if forge {
/*package dev.alinco8.xmxw.platform.forge

import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.ConfigScreen
import dev.alinco8.xmxw.loc
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment

@Mod(XMXWClient.MOD_ID)
class ForgeEntrypointClient {
    init {
        if (FMLEnvironment.dist.isClient) {

            XMXWClient.initialize()

            MinecraftForge.EVENT_BUS.register(this)

            @Suppress("Removal", "Deprecation")
            ModLoadingContext.get().container.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory::class.java
            ) {
                ConfigScreenHandler.ConfigScreenFactory { _, parent ->
                    ConfigScreen.getConfigScreen(parent)
                }
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
*///? }
