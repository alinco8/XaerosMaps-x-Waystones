package dev.alinco8.xaeromaps_waystones

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod.LOGGER
import xaero.hud.minimap.BuiltInHudModules
import java.util.LinkedList
import java.util.Timer
import kotlin.concurrent.schedule
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import xaero.hud.minimap.waypoint.set.WaypointSet

object ManagerWatcher {
    @Volatile
    private var queue = LinkedList<(WaypointSet) -> Unit>()
    @Volatile
    private var watching = false
    private var timer: Timer? = null

    private fun getWaypointSet(dimKey: ResourceKey<Level>? = null): WaypointSet? {
        val waypointSet = BuiltInHudModules.MINIMAP.currentSession?.worldManager

        return if (dimKey == null) {
            waypointSet?.currentWorld?.currentWaypointSet
        } else {
            waypointSet?.currentRootContainer?.allWorldsIterable?.find { world ->
                world.dimId == dimKey
            }?.currentWaypointSet
        }
    }

    fun execute(dimKey: ResourceKey<Level>?, block: (WaypointSet) -> Unit) {
        val waypointSet = getWaypointSet(dimKey) ?: return addToQueue(block)

        LOGGER.debug("Executing immediately, waypointSet found")
        block(waypointSet)
    }

    @Synchronized
    private fun addToQueue(block: (WaypointSet) -> Unit) {
        queue.add(block)
        startWatching()
    }

    @Synchronized
    private fun startWatching() {
        if (watching) return
        LOGGER.debug("Starting to watch for manager")

        watching = true
        val waypointSet = getWaypointSet()

        if (waypointSet == null) {
            timer = Timer()
            timer?.schedule(0L, 500L) {
                val waypointSet = getWaypointSet() ?: return@schedule

                queue.forEach { it(waypointSet) }
                stopWatching()
            }
        }

        LOGGER.debug("startWatching complete")
    }
    @Synchronized
    fun stopWatching() {
        timer?.cancel()
        queue.clear()
        watching = false
        timer = null
    }
}