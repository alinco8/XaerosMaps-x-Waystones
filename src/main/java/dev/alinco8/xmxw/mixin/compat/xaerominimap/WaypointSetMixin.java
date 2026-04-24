package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import static dev.alinco8.xmxw.XMXWClient.LOGGER;

import dev.alinco8.xmxw.XMXWToasts;
import dev.alinco8.xmxw.api.ModdableWaypoint;
import dev.alinco8.xmxw.api.ModdableWaypointSet;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.set.WaypointSet;

@Mixin(value = WaypointSet.class, remap = false)
public abstract class WaypointSetMixin implements ModdableWaypointSet {

    @Shadow
    protected List<Waypoint> list;

    @Unique
    private boolean xmxw$_isModded = false;

    @Override
    public boolean xmxw$isModded() {
        return this.xmxw$_isModded;
    }

    @Override
    public void xmxw$setModded(boolean isModded) {
        this.xmxw$_isModded = isModded;
    }

    @Override
    public void xmxw$addUnchecked(@NotNull Waypoint waypoint) {
        this.list.add(waypoint);
    }

    @Override
    public void xmxw$clearUnchecked() {
        this.list.clear();
    }

    @Inject(
        method = "add(Lxaero/common/minimap/waypoints/Waypoint;Z)V",
        at = @At("HEAD"),
        cancellable = true)
    private void cancelAddIfModded(xaero.common.minimap.waypoints.Waypoint waypoint, boolean front,
        CallbackInfo ci
    ) {
        if (!this.xmxw$_isModded) {
            return;
        }
        ci.cancel();

        LOGGER.debug("Prevented adding waypoint to modded set: {}", waypoint.getName());
        XMXWToasts.moddedWaypointSetNotEditable();
    }

    @Inject(
        method = "addAll(Ljava/util/Collection;Z)V",
        at = @At("HEAD"),
        cancellable = true)
    private void cancelAddAllIfModded(Collection<Waypoint> waypoints, boolean front, CallbackInfo ci
    ) {
        if (!this.xmxw$_isModded) {
            return;
        }
        ci.cancel();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Prevented adding waypoints to modded set: {}",
                waypoints.stream().map(Waypoint::getName).toList());
        }
        XMXWToasts.moddedWaypointSetNotEditable();
    }

    @Inject(
        method = "remove(I)Lxaero/common/minimap/waypoints/Waypoint;",
        at = @At("HEAD"),
        cancellable = true)
    private void cancelRemoveIfModded(int slot,
        CallbackInfoReturnable<Waypoint> cir
    ) {
        cir.cancel();

        if (this.xmxw$_isModded) {
            LOGGER.debug("Prevented removing waypoint from modded set: slot {}", slot);
            XMXWToasts.moddedWaypointSetNotEditable();
            return;
        }

        Waypoint waypoint = this.list.get(slot);
        if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() != null) {
            LOGGER.debug("Prevented removing modded waypoint from modded set: slot {}", slot);
            XMXWToasts.moddedWaypointNotEditable();
        } else {
            this.list.remove(waypoint);
            cir.setReturnValue(waypoint);
        }
    }

    @Inject(
        method = "removeAll",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelRemoveAllIfModded(Collection<Waypoint> waypoints,
        CallbackInfo ci
    ) {
        ci.cancel();

        if (this.xmxw$_isModded) {
            LOGGER.debug("Prevented removing waypoints from modded set: {}",
                waypoints.stream().map(Waypoint::getName).toList());
            XMXWToasts.moddedWaypointSetNotEditable();
            return;
        }

        boolean hasModded = false;
        for (Waypoint waypoint : waypoints) {
            if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() != null) {
                hasModded = true;
                continue;
            }

            this.list.remove(waypoint);
        }

        if (hasModded) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Prevented removing modded waypoints from modded set: {}",
                    waypoints.stream()
                        .filter(wp -> ((ModdableWaypoint) wp).xmxw$getWaystoneId() != null)
                        .map(Waypoint::getName).toList());
            }
            XMXWToasts.moddedWaypointNotEditable();
        }
    }

    @Inject(
        method = "clear",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelClearIfModded(CallbackInfo ci) {
        ci.cancel();

        if (this.xmxw$_isModded) {
            LOGGER.debug("Prevented clearing modded waypoint set");
            XMXWToasts.moddedWaypointSetNotEditable();
            return;
        }

        list.removeIf(
            item -> ((ModdableWaypoint) item).xmxw$getWaystoneId() == null);

        if (!list.isEmpty()) {
            LOGGER.debug(
                "Prevented clearing modded waypoint set because it contains modded waypoints: {}",
                list.stream().map(Waypoint::getName).toList());
            XMXWToasts.moddedWaypointNotEditable();
        }
    }

    @Inject(
        method = "set",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelSetIfModded(int slot, Waypoint waypoint,
        CallbackInfoReturnable<Waypoint> cir
    ) {
        if (((ModdableWaypoint) waypoint).xmxw$getWaystoneId() == null) {
            return;
        }
        cir.cancel();

        LOGGER.debug("Prevented setting modded waypoint in set: slot {}, waypoint {}", slot,
            waypoint.getName());
        XMXWToasts.moddedWaypointNotEditable();
    }
}
