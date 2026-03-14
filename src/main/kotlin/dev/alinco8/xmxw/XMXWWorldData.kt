package dev.alinco8.xmxw

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import dev.alinco8.xmxw.XMXWClient.LOGGER
import dev.alinco8.xmxw.mixin.MinecraftServerAccessor
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.File
import java.util.UUID
import net.minecraft.client.Minecraft

data class XMXWWorldData(
    var waystonePoints: MutableMap<UUID, WaystonePoint> = mutableMapOf()
) {
    @Transient
    lateinit var dataFile: File

    data class WaystonePoint(
        var waystoneId: UUID,
        var hidden: Boolean,
        var cachedName: String,
    )

    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(
                Int2ObjectMap::class.java,
                JsonDeserializer { json, _, context ->
                    val map = Int2ObjectOpenHashMap<WaystonePoint>()
                    val obj = json.asJsonObject
                    obj.entrySet().forEach { (key, value) ->
                        map.put(
                            key.toInt(),
                            context.deserialize(value, WaystonePoint::class.java)
                        )
                    }
                    map
                })
            .registerTypeAdapter(
                Int2ObjectMap::class.java,
                JsonSerializer<Int2ObjectMap<*>> { src, _, context ->
                    // 書き出しは通常のJsonObjectとして
                    val obj = JsonObject()
                    src.forEach { (k, v) ->
                        obj.add(k.toString(), context.serialize(v))
                    }
                    obj
                })
            .create()

        fun currentDataFile(): File? {
            val worldId = Minecraft.getInstance().run {
                (singleplayerServer as? MinecraftServerAccessor)?.storageSource?.levelId
                    ?: (currentServer?.ip ?: return null)
            }.replace(":", "_")

            return Minecraft.getInstance().gameDirectory.resolve("xaero")
                .resolve("waypoints")
                .resolve("$worldId.json")
        }

        fun loadFromCurrentWorld(): XMXWWorldData {
            val dataFile = currentDataFile() ?: error("data file not available")
            val data = if (dataFile.exists()) {
                try {
                    LOGGER.debug("Reading data: {}", dataFile.reader().readText())
                    gson.fromJson(dataFile.reader(), XMXWWorldData::class.java)
                } catch (e: Exception) {
                    LOGGER.error("Failed to load world specific data, creating new one", e)
                    XMXWWorldData()
                }
            } else {
                XMXWWorldData()
            }

            data.dataFile = dataFile

            return data
        }
    }

    fun save() {
        if (!::dataFile.isInitialized) {
            LOGGER.error("Cannot save: dataFile is not initialized!")
            return
        }

        LOGGER.debug("Saving world specific data")

        if (!dataFile.parentFile.exists()) {
            dataFile.parentFile.mkdirs()
        }

        dataFile.writeText(gson.toJson(this))

        LOGGER.debug("Saved world specific data: ${dataFile.absolutePath}")
    }

}
