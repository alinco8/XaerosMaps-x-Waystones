package dev.alinco8.xmxw.api;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface CustomWaypointDataHolder {

    @Nullable UUID xmxw$getWaystoneId();

    void xmxw$setWaystoneId(@Nullable UUID waystoneId);
}
