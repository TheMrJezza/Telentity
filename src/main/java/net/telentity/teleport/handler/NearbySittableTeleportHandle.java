package net.telentity.teleport.handler;

import net.telentity.api.TeHandle;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NearbySittableTeleportHandle implements TeHandle {
    @Override
    public @NotNull String getPermissionName() {
        return "sittable";
    }

    @Override
    public @NotNull String getReasonDescription() {
        return "Entity belongs to the teleporting player and isn't sitting.";
    }

    @Override
    public @NotNull List<? extends Entity> getEntitiesToTeleport(@NotNull Player player) {
        return player.getNearbyEntities(30, 30, 30).stream()
                .map(e -> e instanceof Sittable s ? s : null)
                .map(s -> s instanceof Tameable t ? t : null)
                .filter(Objects::nonNull).filter(tameable -> {
                    final var sittable = (Sittable) tameable;
                    return tameable.getOwner() != null
                            && player.getUniqueId().equals(tameable.getOwner().getUniqueId())
                            && tameable.isTamed() && !sittable.isSitting();
                }).toList();
    }

    @Override
    public void beforeTeleport(@NotNull Player player, @NotNull Entity entity) {
        // unused
    }

    @Override
    public void afterTeleport(@NotNull Player player, @NotNull Entity entity) {
        // unused
    }
}