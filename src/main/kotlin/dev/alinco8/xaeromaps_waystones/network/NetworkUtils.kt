package dev.alinco8.xaeromaps_waystones.network

import net.minecraft.server.level.ServerPlayer

//? if >1.20.1 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

//?} else {
/*import net.minecraft.network.FriendlyByteBuf

*///?}

//? if fabric {
import dev.alinco8.xaeromaps_waystones.loaders.fabric.FabricEntrypoint
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

//?} elif neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor

*///?}

class NetworkUtils {
    companion object {
        fun <T /*? if !forge {*/ : CustomPacketPayload/*?}*/> sendToPlayer(
            player: ServerPlayer,
            packet: T
        ) {
            //? if neoforge {
            /*PacketDistributor.sendToPlayer(
                player,
                packet
            )
            *///?} elif fabric {
            ServerPlayNetworking.send(
                player,
                packet
            )
            //?} else {
            /*dev.alinco8.xaeromaps_waystones.loaders.forge.ForgeNetworking.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with { player },
                packet
            )
            *///?}
        }

        fun <T /*? if !forge {*/ : CustomPacketPayload/*?}*/> sendToAllPlayers(packet: T) {
            //? if neoforge {
            /*PacketDistributor.sendToAllPlayers(packet)
            *///?} elif fabric {
            for (player in PlayerLookup.all(FabricEntrypoint.server)) {
                ServerPlayNetworking.send(
                    player,
                    packet
                )
            }
            //?} else {
            /*dev.alinco8.xaeromaps_waystones.loaders.forge.ForgeNetworking.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                packet
            )
            *///?}
        }
    }
}