package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import dev.alinco8.xmxw.XMXWClient;
import dev.alinco8.xmxw.XMXWWorldData;
import dev.alinco8.xmxw.api.CustomWaypointDataHolder;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointReader;

@Mixin(value = WaypointReader.class, remap = false)
class WaypointReaderMixin {

    @Inject(
        method = "getRightClickOptions(Lxaero/map/mods/gui/Waypoint;Lxaero/map/gui/IRightClickableElement;)Ljava/util/ArrayList;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getRightInjectRightClickOptions(Waypoint element, IRightClickableElement target,
        CallbackInfoReturnable<ArrayList<RightClickOption>> cir
    ) {
        UUID waystoneId = ((CustomWaypointDataHolder) element).xmxw$getWaystoneId();
        if (waystoneId == null) {return;}
        cir.cancel();

        ArrayList<RightClickOption> rightClickOptions = new ArrayList<>();

        rightClickOptions.add(
            new RightClickOption(element.getName(), 0, target) {
                public void onAction(Screen screen) {
                    xmxw$displayUneditableMessage();
                }
            });

        rightClickOptions.add(
            new RightClickOption(
                String.format("X: %d, Y: %s, Z: %d", element.getX(), element.getY(),
                    element.getZ()),
                rightClickOptions.size(), target) {
                public void onAction(Screen screen) {
                    xmxw$displayUneditableMessage();
                }
            });

        rightClickOptions.add((new RightClickOption("gui.xaero_right_click_waypoint_teleport",
            rightClickOptions.size(), target) {
            public void onAction(Screen screen) {
                SupportMods.xaeroMinimap.teleportToWaypoint(screen, element);
            }

            public boolean isActive() {
                return SupportMods.xaeroMinimap.canTeleport(
                    SupportMods.xaeroMinimap.getWaypointWorld());
            }
        }).setNameFormatArgs("T"));
        rightClickOptions.add(
            new RightClickOption("gui.xaero_right_click_waypoint_share", rightClickOptions.size(),
                target) {
                public void onAction(Screen screen) {
                    SupportMods.xaeroMinimap.shareWaypoint(element, (GuiMap) screen,
                        SupportMods.xaeroMinimap.getWaypointWorld());
                }
            });
        rightClickOptions.add(
            new RightClickOption("Hide waypoint", rightClickOptions.size(), target) {
                public void onAction(Screen screen) {
                    XMXWWorldData worldData = XMXWClient.INSTANCE.getWorldData();
                    if (worldData == null) {
                        XMXWClient.LOGGER.warn("No world data found");
                        return;
                    }

                    var w = worldData.getWaystonePoints().get(waystoneId);
                    if (w == null) {
                        XMXWClient.LOGGER.warn("No waystone found: {}", waystoneId);
                        return;
                    }

                    w.setHidden(true);
                    var level = Minecraft.getInstance().level;
                    if (level != null) {
                        //? if >=1.21.10 {
                            /*XMXWClient.INSTANCE.updateWaystoneWaypoints(
                                level.dimension().identifier());
                             *///? } else {
                        XMXWClient.INSTANCE.updateWaystoneWaypoints(
                            level.dimension().location());
                        //? }
                    }
                    SupportMods.xaeroMinimap.disableWaypoint(element);
                }
            });

        cir.setReturnValue(rightClickOptions);
    }

    @Unique
    private void xmxw$displayUneditableMessage() {
        XMXWClient.displayMessage(
            Component.translatable("xmxw.messages.custom_waypoint_uneditable"));
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.clientSideCloseContainer();
        }
    }
}
