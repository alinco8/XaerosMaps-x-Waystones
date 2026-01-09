//? if fabric {
/*package dev.alinco8.xmxw.platform.fabric

import dev.alinco8.xmxw.XMXWClient
import dev.alinco8.xmxw.loc
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents

class FabricEntrypointClient : ClientModInitializer {
    override fun onInitializeClient() {
        XMXWClient.initialize()

        ClientPlayConnectionEvents.JOIN.register { _, _, minecraft ->
            XMXWClient.onDimensionChange(
                minecraft.level?.dimension()?.loc() ?: return@register
            )
        }
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, level ->
            XMXWClient.onDimensionChange(
                level.dimension().loc()
            )
        }
    }
}
*///? }