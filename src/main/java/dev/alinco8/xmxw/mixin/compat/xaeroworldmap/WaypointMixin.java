package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import dev.alinco8.xmxw.api.WaypointExtraHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xaero.map.mods.gui.Waypoint;

@Mixin(Waypoint.class)
public class WaypointMixin implements WaypointExtraHolder {

    @Unique
    private Boolean xmxw$isCustom = null;

    @Override
    public @Nullable Boolean xmxw$getIsCustom() {
        return xmxw$isCustom;
    }

    @Override
    public void xmxw$setIsCustom(@Nullable Boolean isCustom) {
        this.xmxw$isCustom = isCustom;
    }
}
