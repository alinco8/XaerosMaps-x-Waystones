package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.XMXWClient;
import dev.alinco8.xmxw.XMXWToasts;
import dev.alinco8.xmxw.api.ModdableWaypointSet;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;

@Mixin(value = MinimapWorld.class, remap = false)
public class MinimapWorldMixin {

    @Shadow
    @Final
    protected Map<String, WaypointSet> waypointSets;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void afterInit(MinimapWorldContainer container, String node, ResourceKey<Level> dimId,
        CallbackInfo ci
    ) {
        WaypointSet waypointSet = WaypointSet.Builder.begin()
            .setName(XMXWClient.XMXW_WAYPOINT_SET_NAME).build();
        ((ModdableWaypointSet) (Object) waypointSet).xmxw$setModded(true);

        this.waypointSets.put(XMXWClient.XMXW_WAYPOINT_SET_NAME, waypointSet);
    }

    @Inject(
        method = "removeWaypointSet",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelRemoveIfModded(String key, CallbackInfoReturnable<WaypointSet> cir) {
        if (!((ModdableWaypointSet) (Object) this.waypointSets.get(key)).xmxw$isModded()) {
            return;
        }
        cir.cancel();

        XMXWClient.LOGGER.debug("Prevented removing a modded waypoint set");
        XMXWToasts.moddedWaypointSetNotEditable();
    }

    @Inject(
        method = "addWaypointSet(Lxaero/hud/minimap/waypoint/set/WaypointSet;)Lxaero/hud/minimap/waypoint/set/WaypointSet;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void cancelAddIfModded(WaypointSet set, CallbackInfoReturnable<WaypointSet> cir) {
        if (!((ModdableWaypointSet) (Object) this.waypointSets.get(
            set.getName())).xmxw$isModded()) {
            return;
        }
        cir.cancel();

        XMXWClient.LOGGER.debug(
            "Prevented adding a waypoint set with the same name as a modded one");
        XMXWToasts.moddedWaypointSetNotEditable();
    }
}
