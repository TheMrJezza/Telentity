package net.telentity.teleport.handler;

import net.telentity.api.TeHandle;
import net.telentity.api.tools.EntityTools;
import net.telentity.api.watcher.Unmount;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VehicleTeleportHandle implements TeHandle {

    private final Unmount unmount;
    private final EntityTools entityTools;

    public VehicleTeleportHandle(Unmount unmount, EntityTools entityTools) {
        this.unmount = unmount;
        this.entityTools = entityTools;
    }

    @Override
    public @NotNull String getPermissionName() {
        return "vehicle";
    }

    @Override
    public @NotNull String getReasonDescription() {
        return "Entity is the vehicle of the teleporting player.";
    }

    @Override
    public @NotNull List<? extends Entity> getEntitiesToTeleport(@NotNull Player player) {
        final var vehicle = player.isInsideVehicle() ? player.getVehicle() : unmount.lastUnmount(player);
        return vehicle == null ? List.of() : List.of(vehicle);
    }

    @Override
    public void beforeTeleport(@NotNull Player player, @NotNull Entity entity) {
        // unused here, but a good example of what this does is in the LeashTeleportHandle
    }

    @Override
    public void afterTeleport(@NotNull Player player, @NotNull Entity entity) {
        entityTools.getEntityPassengerTools().addPassenger(entity, player);
    }
}
