package dev.alinco8.xmxw

import com.google.gson.Gson
import dev.alinco8.xmxw.XMXWClient.LOGGER
import dev.alinco8.xmxw.mixin.MinecraftServerAccessor
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
        val gson: Gson = Gson()

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
                    dataFile.bufferedReader().use { reader ->
                        gson.fromJson(reader, XMXWWorldData::class.java)
                    }
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
