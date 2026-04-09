package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.XMXWClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.hud.minimap.waypoint.WaypointTeleport;
import xaero.hud.minimap.world.MinimapDimensionHelper;
import xaero.hud.minimap.world.MinimapWorld;

@Mixin(value = WaypointTeleport.class, remap = false)
public class WaypointTeleportMixin {

    @ModifyVariable(
        method = "teleportToWaypoint(Lxaero/common/minimap/waypoints/Waypoint;Lxaero/hud/minimap/world/MinimapWorld;Lnet/minecraft/client/gui/screens/Screen;Z)V",
        at = @At(value = "STORE", ordinal = 0),
        name = "fullCommand",
        require = 1
    )
    private String injectDimensionId(String fullCommand) {
        return XMXWClient.customDimension != null ?
            "/execute in " + XMXWClient.customDimension + " run " : "";
    }

    @Redirect(
        method = "teleportToWaypoint(Lxaero/common/minimap/waypoints/Waypoint;Lxaero/hud/minimap/world/MinimapWorld;Lnet/minecraft/client/gui/screens/Screen;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lxaero/hud/minimap/world/MinimapDimensionHelper;getDimensionDivision(Lxaero/hud/minimap/world/MinimapWorld;)D"
        )
    )
    private double injectDimDiv(MinimapDimensionHelper instance,
        MinimapWorld minimapWorld
    ) {
        return XMXWClient.customDimension == null ? instance.getDimensionDivision(
            minimapWorld) : 1;
    }
}
