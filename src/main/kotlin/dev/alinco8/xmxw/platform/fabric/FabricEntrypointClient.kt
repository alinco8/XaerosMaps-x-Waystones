//? if fabric {
/*package dev.alinco8.xmxw.platform.fabric

import dev.alinco8.xmxw.UpdateChecker
import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.loc
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component

class FabricEntrypointClient : ClientModInitializer {
    private var lastWorld: ClientLevel? = null

    override fun onInitializeClient() {
        XMXWClient.initialize()

        val modContainer = FabricLoader.getInstance()
            .getModContainer(XMXWClient.MOD_ID).orElseThrow()
        
        ClientPlayConnectionEvents.JOIN.register { _, _, minecraft ->
            XMXWClient.onDimensionChange(
                minecraft.level?.dimension()?.loc() ?: return@register
            )

            val currentVersion = modContainer.metadata.version.friendlyString
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
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val level = client.level ?: return@register
            if (level == lastWorld) return@register
            lastWorld = level
            XMXWClient.onDimensionChange(
                level.dimension().loc()
            )
        }
    }
}
*///? }
