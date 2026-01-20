//? if forge {
/*package dev.alinco8.xmxw.platform.forge

import dev.alinco8.xmxw.UpdateChecker
import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.XMXWClient.LOGGER
import dev.alinco8.xmxw.config.ConfigScreen
import dev.alinco8.xmxw.loc
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.VersionChecker
import net.minecraftforge.fml.loading.FMLEnvironment

@Mod(XMXWClient.MOD_ID)
class ForgeEntrypointClient {
    @Suppress("Removal", "Deprecation")
    val modContainer: ModContainer = ModLoadingContext.get().container

    init {
        if (FMLEnvironment.dist.isClient) {
            XMXWClient.initialize()

            MinecraftForge.EVENT_BUS.register(this)

            modContainer.registerExtensionPoint(
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

        val currentVersion = modContainer.modInfo.version.toString()
        UpdateChecker.checkUpdate(currentVersion)?.let {
            event.entity.displayClientMessage(
                Component.translatable(
                    "xmxw.messages.mod_update.available",
                    it,
                    currentVersion,
                ),
                false
            )
        }
    }
}
*///? }
