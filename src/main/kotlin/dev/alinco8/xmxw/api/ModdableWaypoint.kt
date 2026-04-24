package dev.alinco8.xmxw.api

import java.util.*

interface ModdableWaypoint {
    fun `xmxw$getWaystoneId`(): UUID?

    fun `xmxw$setWaystoneId`(waystoneId: UUID?)
}
