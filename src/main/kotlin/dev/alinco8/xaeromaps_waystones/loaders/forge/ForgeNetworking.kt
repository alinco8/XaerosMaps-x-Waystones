//? if forge {
/*package dev.alinco8.xaeromaps_waystones.loaders.forge

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod.MOD_ID
import dev.alinco8.xaeromaps_waystones.network.WaystoneActivatedPacket
import dev.alinco8.xaeromaps_waystones.network.WaystoneRemovedPacket
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

class ForgeNetworking {
    companion object {
        const val PROTOCOL_VERSION = "1"
        val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"),
            { PROTOCOL_VERSION },
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        )
        var id = 0

        fun register() {
            INSTANCE.messageBuilder(WaystoneActivatedPacket::class.java, id++)
                .encoder(WaystoneActivatedPacket::encode)
                .decoder(WaystoneActivatedPacket::decode)
                .consumerMainThread(WaystoneActivatedPacket::handle)
                .add()
            INSTANCE.messageBuilder(WaystoneRemovedPacket::class.java, id++)
                .encoder(WaystoneRemovedPacket::encode)
                .decoder(WaystoneRemovedPacket::decode)
                .consumerMainThread(WaystoneRemovedPacket::handle)
                .add()
        }
    }
}
*///?}