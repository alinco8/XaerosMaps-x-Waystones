package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.api.ModdableWaypoint;
import dev.alinco8.xmxw.api.ModdableWaypointSet;
import java.util.stream.StreamSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.io.WaypointIO;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

@Mixin(value = WaypointIO.class, remap = false)
public class WaypointIOMixin {

    @Redirect(
        method = "saveWaypoints",
        at = @At(
            value = "INVOKE",
            target = "Lxaero/hud/minimap/world/MinimapWorld;getIterableWaypointSets()Ljava/lang/Iterable;"
        )
    )
    private Iterable<WaypointSet> filterWaypointSets(MinimapWorld instance) {
        return StreamSupport.stream(instance.getIterableWaypointSets().spliterator(), false)
            .filter(set -> !((ModdableWaypointSet) (Object) set).xmxw$isModded())
            .toList();
    }

    @Redirect(
        method = "saveWaypoints",
        at = @At(
            value = "INVOKE",
            target = "Lxaero/hud/minimap/waypoint/set/WaypointSet;getWaypoints()Ljava/lang/Iterable;"
        )
    )
    private Iterable<Waypoint> filterWaypoints(WaypointSet instance) {
        return StreamSupport.stream(instance.getWaypoints().spliterator(), false)
            .filter(w -> ((ModdableWaypoint) w).xmxw$getWaystoneId() == null)
            .toList();
    }
}
