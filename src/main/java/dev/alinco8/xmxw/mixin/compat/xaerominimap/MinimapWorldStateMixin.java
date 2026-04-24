package dev.alinco8.xmxw.mixin.compat.xaerominimap;

import dev.alinco8.xmxw.XMXWClient;
import java.util.Objects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.hud.minimap.world.state.MinimapWorldState;
import xaero.hud.path.XaeroPath;

@Mixin(value = MinimapWorldState.class, remap = false)
public class MinimapWorldStateMixin {

    @Shadow
    private XaeroPath autoWorldPath;

    @Inject(
        method = "setAutoWorldPath",
        at = @At("HEAD")
    )
    private void onSetAutoWorldPath(XaeroPath autoWorldPath, CallbackInfo ci) {
        if (!Objects.equals(this.autoWorldPath != null ? this.autoWorldPath.toString() : null,
            autoWorldPath.toString())) {
            this.autoWorldPath = autoWorldPath;
            XMXWClient.onXaeroWorldChanged();
        }
    }
}
