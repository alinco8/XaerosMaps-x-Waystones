package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import dev.alinco8.xmxw.api.CustomWaypointDataHolder;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xaero.common.minimap.waypoints.Waypoint;

@Mixin(value = Waypoint.class, remap = false)
public class MinimapWaypointMixin implements CustomWaypointDataHolder {

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
}
