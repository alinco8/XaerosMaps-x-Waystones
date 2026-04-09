package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.XMXWClient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.hud.minimap.world.MinimapDimensionHelper;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;

@Mixin(value = MinimapDimensionHelper.class, remap = false)
public class MinimapDimensionHelperMixin {

    @Inject(
        method = "getDimCoordinateScale",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getDimCoordinateScale(MinimapWorld minimapWorld,
        CallbackInfoReturnable<Double> cir
    ) {
        if (XMXWClient.customDimension != null) {
            cir.cancel();

            ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION,
                XMXWClient.customDimension);
            MinimapWorldRootContainer rootContainer = minimapWorld.getContainer().getRoot();

            cir.setReturnValue(
                rootContainer.getDimensionScale(dimKey)
            );
        }
    }
}
