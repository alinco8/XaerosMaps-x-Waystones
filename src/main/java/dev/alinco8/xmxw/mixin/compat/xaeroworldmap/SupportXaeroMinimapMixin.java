package dev.alinco8.xmxw.mixin.compat.xaeroworldmap;

import dev.alinco8.xmxw.api.WaypointExtraHolder;
import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.single.SingleConfigManager;
import xaero.map.WorldMap;
import xaero.map.config.primary.option.WorldMapPrimaryClientConfigOptions;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;

//? if >=1.21.6 {
/*import xaero.hud.minimap.waypoint.set.WaypointSet;
 *///? } else {
import xaero.common.minimap.waypoints.WaypointSet;
    //? }

@Mixin(SupportXaeroMinimap.class)
abstract
class SupportXaeroMinimapMixin {

    @Shadow
    private WaypointSet waypointSet;

    @Shadow
    public abstract Waypoint convertWaypoint(xaero.common.minimap.waypoints.Waypoint w,
        boolean editable, String setName, double dimDiv
    );

    @Redirect(
        method = "convertWaypoints",
        at = @At(
            value = "NEW",
            target = "java/util/ArrayList"
        )
    )
    public ArrayList<Waypoint> convertWaypoints(
        double dimDiv
    ) {
        if (this.waypointSet == null) {return new ArrayList<>();}

        return xmxw$getCustomWaypoints(dimDiv);
    }

    @Unique
    public ArrayList<Waypoint> xmxw$getCustomWaypoints(double dimDiv) {
        ArrayList<Waypoint> result = new ArrayList<>();

        Iterable<xaero.common.minimap.waypoints.Waypoint> list = BuiltInHudModules.MINIMAP.getCurrentSession()
            .getWorldManager()
            .getCustomWaypoints();
        ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        SingleConfigManager<Config> primaryConfigManager = configManager.getPrimaryConfigManager();
        boolean showingDisabled = primaryConfigManager.getEffective(
            WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);

        for (xaero.common.minimap.waypoints.Waypoint w : list) {
            if (showingDisabled || !w.isDisabled()) {
                Waypoint convertedWaypoint = this.convertWaypoint(w, true, "Custom Waypoints",
                    dimDiv);
                ((WaypointExtraHolder) convertedWaypoint).xmxw$setIsCustom(true);
                result.add(convertedWaypoint);
            }
        }

        return result;
    }
}
