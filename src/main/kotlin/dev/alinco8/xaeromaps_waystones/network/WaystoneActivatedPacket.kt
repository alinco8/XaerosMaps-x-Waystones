package dev.alinco8.xaeromaps_waystones.network

import dev.alinco8.xaeromaps_waystones.WaystonePointHelper
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf

import dev.alinco8.xaeromaps_waystones.config.Config

//? if forge {
//?} else {
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import net.minecraft.resources.ResourceLocation

//?}

data class WaystoneActivatedPacket(
    val pos: BlockPos,
    val name: String,
    val loc: String
)/*? if !forge {*/ : CustomPacketPayload/*?}*/ {
    companion object {
        //? if forge {
        /*fun encode(packet: WaystoneActivatedPacket, buf: FriendlyByteBuf) {
            buf.writeBlockPos(packet.pos)
            buf.writeUtf(packet.name)
            buf.writeUtf(packet.loc)
        }

        fun decode(buf: FriendlyByteBuf): WaystoneActivatedPacket {
            val pos = buf.readBlockPos()
            val name = buf.readUtf(Short.MAX_VALUE.toInt())
            val loc = buf.readUtf(Short.MAX_VALUE.toInt())
            return WaystoneActivatedPacket(pos, name, loc)
        }
        *///?} else {
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
        //?}


        fun handle(
            packet: WaystoneActivatedPacket,
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

    //? if forge {
    //?} else {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
    //?}
}