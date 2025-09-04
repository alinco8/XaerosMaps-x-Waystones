package dev.alinco8.xaeromaps_waystones.packets

import dev.alinco8.xaeromaps_waystones.WaystonePointHelper
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

//? if neoforge {
/*import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.network.handling.IPayloadContext
*///?} elif fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
//?}

data class WaystoneRemovedPacket(
    val pos: BlockPos,
    val name: String,
) : CustomPacketPayload {

    companion object {
        const val INVALID = "invalid"

        val TYPE: CustomPacketPayload.Type<WaystoneRemovedPacket> =
            CustomPacketPayload.Type<WaystoneRemovedPacket>(
                ResourceLocation.fromNamespaceAndPath(
                    XaerosMapsWaystonesMod.MOD_ID,
                    "waystone_removed_packet"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, WaystoneRemovedPacket> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC, WaystoneRemovedPacket::pos,
                ByteBufCodecs.STRING_UTF8, WaystoneRemovedPacket::name,
                ::WaystoneRemovedPacket
            )

        fun handle(
            packet: WaystoneRemovedPacket,
            //? if neoforge {
            /*context: IPayloadContext,
            *///?} elif fabric {
            context: ClientPlayNetworking.Context,
            //?}
        ) {
            //? if neoforge {
            /*context.enqueueWork {
                if (FMLEnvironment.dist.isClient) {
                    handlePacket(packet)
                }
            }
            *///?} elif fabric {
            context.client().execute {
                handlePacket(packet)
            }
            //?}
        }

        private fun handlePacket(packet: WaystoneRemovedPacket) {
            if (packet.name == INVALID) return

            WaystonePointHelper.removeWaypoint(
                packet.pos.x,
                packet.pos.y,
                packet.pos.z
            )
        }
    }


    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}