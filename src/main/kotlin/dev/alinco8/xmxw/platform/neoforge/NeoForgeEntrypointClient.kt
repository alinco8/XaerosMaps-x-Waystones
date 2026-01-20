//? if neoforge {
/*package dev.alinco8.xmxw.platform.neoforge

import dev.alinco8.xmxw.UpdateChecker
import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.config.ConfigScreen
import dev.alinco8.xmxw.loc
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent

//? if >=1.20.5 {
/*import net.neoforged.neoforge.client.gui.IConfigScreenFactory

*///? } else {
import net.neoforged.neoforge.client.ConfigScreenHandler as IConfigScreenFactory

//? }

//? if >=1.20.5 {
/*import net.neoforged.api.distmarker.Dist
@Mod(XMXWClient.MOD_ID, dist = [Dist.CLIENT])
*///? } else {
@Mod(XMXWClient.MOD_ID)
//? }
class NeoForgeEntrypointClient(val modContainer: ModContainer) {
    init {
        if (/*?>=1.21.1{*/ /*true *//*?}else{*/net.neoforged.fml.loading.FMLEnvironment.dist.isClient/*?}*/) {
            XMXWClient.initialize()

            NeoForge.EVENT_BUS.register(this)

            //? if >=1.20.5 {
            /*modContainer.registerExtensionPoint(IConfigScreenFactory::class.java) {
                IConfigScreenFactory { _, parent ->
                    ConfigScreen.getConfigScreen(parent)
                }
            }
            *///? } else {
            modContainer.registerExtensionPoint(
                IConfigScreenFactory.ConfigScreenFactory::class.java
            ) {
                IConfigScreenFactory.ConfigScreenFactory { _, parent ->
                    ConfigScreen.getConfigScreen(parent)
                }
            }
            //? }
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
