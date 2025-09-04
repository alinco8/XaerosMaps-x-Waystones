package dev.alinco8.xaeromaps_waystones.config

import com.google.gson.GsonBuilder
import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod.MOD_ID
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import net.minecraft.resources.ResourceLocation
import xaero.hud.minimap.waypoint.WaypointColor

//? if neoforge {
/*import net.neoforged.fml.loading.FMLPaths
*///?} else if fabric {
import net.fabricmc.loader.api.FabricLoader
//?}

class Config {
    companion object {
        val INSTANCE: ConfigClassHandler<Config> =
            ConfigClassHandler.createBuilder<Config>(Config::class.java)
                .id(ResourceLocation.fromNamespaceAndPath(MOD_ID, "config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        //? if neoforge {
                        /*.setPath(FMLPaths.CONFIGDIR.get().resolve("${MOD_ID}.json5"))
                        *///?} elif fabric {
                        .setPath(FabricLoader.getInstance().configDir.resolve("${MOD_ID}.json5"))
                        //?}
                        .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                        .setJson5(true)
                        .build()
                }
                .build()
    }

    @SerialEntry
    var waypointIcon = "☰"

    @SerialEntry
    var waypointNameTemplate = "{waystone_name} [Waystone]"

    @SerialEntry
    var waypointColorFallback = WaypointColor.GRAY

    @SerialEntry
    var waypointColors: MutableMap<String, WaypointColor> = mutableMapOf(
        "waystones:warp_plate" to WaypointColor.YELLOW,
    )
}