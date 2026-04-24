package dev.alinco8.xmxw.api

import xaero.common.minimap.waypoints.Waypoint

interface ModdableWaypointSet {
    fun `xmxw$isModded`(): Boolean

    fun `xmxw$setModded`(isModded: Boolean)

    fun `xmxw$addUnchecked`(waypoint: Waypoint)

    fun `xmxw$clearUnchecked`()
}
