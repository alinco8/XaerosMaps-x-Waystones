package dev.alinco8.xaeromaps_waystones.network

import dev.alinco8.xaeromaps_waystones.WaystonePointHelper
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf

//? if forge {
//?} else {
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import net.minecraft.resources.ResourceLocation

//?}

data class WaystoneRemovedPacket(
    val pos: BlockPos,
    val name: String,
    val loc: String
)/*? if !forge {*/ : CustomPacketPayload/*?}*/ {
    companion object {
        //? if forge {
        /*fun encode(packet: WaystoneRemovedPacket, buf: FriendlyByteBuf) {
            buf.writeBlockPos(packet.pos)
            buf.writeUtf(packet.name)
            buf.writeUtf(packet.loc)
        }

        fun decode(buf: FriendlyByteBuf): WaystoneRemovedPacket {
            val pos = buf.readBlockPos()
            val name = buf.readUtf(Short.MAX_VALUE.toInt())
            val loc = buf.readUtf(Short.MAX_VALUE.toInt())
            return WaystoneRemovedPacket(pos, name, loc)
        }
        *///?} else {
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
                ByteBufCodecs.STRING_UTF8, WaystoneRemovedPacket::loc,
                ::WaystoneRemovedPacket
            )
        //?}


        fun handle(
            packet: WaystoneRemovedPacket,
        ) {
            WaystonePointHelper.removeWaypoint(
                packet.pos.x,
                packet.pos.y,
                packet.pos.z,
                null
            )
        }
    }

    //? if forge {
    //?} else {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
    //?}
}