package dev.alinco8.xmxw.config

//? if >=1.21.5 {
/*import xaero.hud.minimap.waypoint.WaypointVisibilityType
*///? } else {
import xaero.common.minimap.waypoints.WaypointVisibilityType
//? }

import dev.alinco8.xmxw.XMXWClient
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import xaero.hud.minimap.waypoint.WaypointColor

class XMXWConfig {
    companion object {
        @JvmStatic
        val HANDLER: ConfigClassHandler<XMXWConfig> =
            ConfigClassHandler
                .createBuilder(XMXWConfig::class.java)
                .id(XMXWClient.loc("config"))
                .serializer { config ->
                    //? if fabric {
                    /*val configPath =
                        net.fabricmc.loader.api.FabricLoader.getInstance().configDir.resolve("config.json")
                     */
                    //? } else if neoforge {
                    val configPath =
                        net.neoforged.fml.loading.FMLPaths.CONFIGDIR
                            .get()
                            .resolve("${XMXWClient.MOD_ID}.json")
                    //? } else if forge {
                    /*val configPath = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get()
                        .resolve("${XMXWClient.MOD_ID}.json")
                     */
                    //? }

                    GsonConfigSerializerBuilder
                        .create(config)
                        .setPath(configPath)
                        .setJson5(true)
                        .build()
                }.build()
    }

    @SerialEntry
    var waypointTitle = "☰"

    @SerialEntry
    var waypointNameFormat = "{name} [W]"

    @SerialEntry
    var waypointColor = WaypointColor.GRAY

    @SerialEntry
    var waypointVisibility = WaypointVisibilityType.LOCAL

    @SerialEntry
    var waypointOffsetX = 0

    @SerialEntry
    var waypointOffsetY = 0

    @SerialEntry
    var waypointOffsetZ = 0
}
