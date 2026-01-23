//? if forge {
/*package dev.alinco8.xmxw.platform.forge

import dev.alinco8.xmxw.UpdateChecker
import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.ConfigScreen
import dev.alinco8.xmxw.loc
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment

@Mod(XMXWClient.MOD_ID)
class ForgeEntrypointClient {
    @Suppress("Removal", "Deprecation")
    val modContainer: ModContainer = ModLoadingContext.get().container
    private var lastWorld: ClientLevel? = null

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
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        val minecraft = Minecraft.getInstance()
        val level = minecraft.level ?: return
        if (level == lastWorld) return
        lastWorld = level

        XMXWClient.onDimensionChange(
            level.dimension().loc()
        )
    }
    @SubscribeEvent
    fun onClientWorldChange(event: PlayerEvent.PlayerLoggedInEvent) {
        XMXWClient.onDimensionChange(
            event.entity.level().dimension().loc()
        )

        val currentVersion = modContainer.modInfo.version.toString()
        UpdateChecker.checkUpdate(currentVersion)?.let {
            XMXWClient.displayMessage(
                Component.translatable(
                    "xmxw.messages.mod_update_available",
                    it,
                    currentVersion,
                )
            )
        }
    }
}
*///? }
