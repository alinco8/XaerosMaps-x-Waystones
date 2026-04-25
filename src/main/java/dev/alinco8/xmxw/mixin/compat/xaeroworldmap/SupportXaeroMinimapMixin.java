package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import com.llamalad7.mixinextras.sugar.Local;
import dev.alinco8.xmxw.api.ModdableWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;

//? if >=1.21.6 {
/*import xaero.hud.minimap.waypoint.set.WaypointSet;
 *///? } else {

    //? }

@Mixin(value = SupportXaeroMinimap.class, remap = false)
abstract
class SupportXaeroMinimapMixin {

    @ModifyVariable(
        method = "convertWaypoint",
        at = @At("HEAD"),
        argsOnly = true,
        index = 4
    )
    private double modifyDimDiv(double dimDiv,
        @Local(argsOnly = true, ordinal = 0) xaero.common.minimap.waypoints.Waypoint w
    ) {
        return ((ModdableWaypoint) w).xmxw$getWaystoneId() != null ? 1.0 : dimDiv;
    }

    @Inject(
        method = "convertWaypoint",
        at = @At(
            value = "RETURN"
        )
    )
    private void setWaystoneIdToWaypoint(xaero.common.minimap.waypoints.Waypoint w,
        boolean editable,
        String setName, double dimDiv, CallbackInfoReturnable<Waypoint> cir
    ) {
        var waystoneId = ((ModdableWaypoint) w).xmxw$getWaystoneId();
        if (waystoneId == null) {return;}

        ((ModdableWaypoint) cir.getReturnValue()).xmxw$setWaystoneId(waystoneId);
    }

    @Inject(
        method = "disableWaypoint",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelDisableIfModded(Waypoint waypoint, CallbackInfo ci) {
        if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() == null) {return;}

        ci.cancel();
    }

    @Inject(
        method = "deleteWaypoint",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelDeleteIfModded(Waypoint waypoint, CallbackInfo ci) {
        if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() == null) {return;}

        ci.cancel();
    }

    @Inject(
        method = "toggleTemporaryWaypoint",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelToggleIfModded(Waypoint waypoint, CallbackInfo ci) {
        if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() == null) {return;}

        ci.cancel();
    }
}
