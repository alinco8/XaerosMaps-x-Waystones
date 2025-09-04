package dev.alinco8.xaeromaps_waystones.packets;

import dev.alinco8.xaeromaps_waystones.WaystonePointHelper
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import dev.alinco8.xaeromaps_waystones.config.Config
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

data class WaystoneActivatedPacket(
    val pos: BlockPos,
    val name: String,
    val loc: String
) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WaystoneActivatedPacket> =
            CustomPacketPayload.Type<WaystoneActivatedPacket>(
                ResourceLocation.fromNamespaceAndPath(
                    XaerosMapsWaystonesMod.MOD_ID,
                    "waystone_activated_packet"
                )
            )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, WaystoneActivatedPacket> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC, WaystoneActivatedPacket::pos,
                ByteBufCodecs.STRING_UTF8, WaystoneActivatedPacket::name,
                ByteBufCodecs.STRING_UTF8, WaystoneActivatedPacket::loc,
                ::WaystoneActivatedPacket
            )

        fun handle(
            packet: WaystoneActivatedPacket,
            //? if neoforge {
            /*context: IPayloadContext,
            *///?} elif fabric {
            context: ClientPlayNetworking.Context,
            //?}
        ) {
            val instance = Config.INSTANCE.instance()

            WaystonePointHelper.createOrUpdateWaystonePoint(
                instance.waypointNameTemplate.replace(
                    "{waystone_name}",
                    packet.name
                ),
                instance.waypointIcon,
                WaystonePointHelper.waypointColorFromWaystone(packet.loc),
                packet.pos.x,
                packet.pos.y,
                packet.pos.z,
                null
            )
        }
    }


    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }
}