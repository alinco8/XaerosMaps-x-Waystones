package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import dev.alinco8.xmxw.XMXWClient;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.world.MapWorld;

@Mixin(value = MapWorld.class, remap = false)
public class MapWorldMixin {

    @Inject(
        method = "setCustomDimensionId",
        at = @At("RETURN")
    )
    private void onCustomDimensionIdChanged(ResourceKey<Level> dimension,
        CallbackInfo ci
    ) {
        XMXWClient.INSTANCE.onCustomDimensionChanged(dimension);
    }
}
