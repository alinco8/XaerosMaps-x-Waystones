package dev.alinco8.xmxw.config

import dev.alinco8.xmxw.XMXWClient
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import xaero.hud.minimap.waypoint.WaypointColor

class XMXWConfig {
    companion object {
        val HANDLER: ConfigClassHandler<XMXWConfig> =
            ConfigClassHandler.createBuilder(XMXWConfig::class.java)
                .id(XMXWClient.loc("config"))
                .serializer { config ->
                    //? if fabric {
                    /*val configPath =
                        net.fabricmc.loader.api.FabricLoader.getInstance().configDir.resolve("config.json")
                    *///? } else if neoforge {
                    val configPath = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get()
                        .resolve("${XMXWClient.MOD_ID}.json")
                    //? }

                    GsonConfigSerializerBuilder.create(config)
                        .setPath(configPath)
                        .setJson5(true)
                        .build()
                }
                .build()
    }

    @SerialEntry
    var waypointTitle: String = "☰"

    @SerialEntry
    var waypointNameFormat: String = "{name} [W]"

    @SerialEntry
    var waypointColor: WaypointColor = WaypointColor.GRAY
}