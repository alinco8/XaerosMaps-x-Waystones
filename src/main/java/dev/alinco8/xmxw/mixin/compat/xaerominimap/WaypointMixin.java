package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.XMXWClient;
import dev.alinco8.xmxw.XMXWToasts;
import dev.alinco8.xmxw.api.ModdableWaypoint;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;

@Mixin(value = Waypoint.class, remap = false)
public abstract class WaypointMixin implements ModdableWaypoint {

    @Shadow
    public abstract String getName();

    @Unique
    private @Nullable UUID xmxw$waystoneId;

    @Override
    public @Nullable UUID xmxw$getWaystoneId() {
        return this.xmxw$waystoneId;
    }

    @Override
    public void xmxw$setWaystoneId(@Nullable UUID waystoneId) {
        this.xmxw$waystoneId = waystoneId;
    }

    @Inject(
        method = {"setTemporary", "setDisabled", "setName", "setInitials", "setPurpose",
            "setRotation", "setVisibility", "setWaypointColor", "setX", "setY", "setYaw",
            "setYIncluded", "setZ", "setColor", "setOneoffDestination", "setSymbol", "setType",
            "setVisibilityType"},
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelIfModded(CallbackInfo ci) {
        if (xmxw$waystoneId == null) {return;}
        ci.cancel();

        if (XMXWClient.LOGGER.isDebugEnabled()) {
            var calledMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
            XMXWClient.LOGGER.debug("Prevented editing of modded waypoint {} {} (via {}).",
                this.xmxw$waystoneId, this.getName(), calledMethod);
        }
        XMXWToasts.moddedWaypointNotEditable();
    }
}
