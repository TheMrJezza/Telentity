package net.telentity.teleport.handler;

import net.telentity.api.TeHandle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class LeashTeleportHandle implements TeHandle {

    @Override
    public @NotNull String getPermissionName() {
        return "leashed";
    }

    @Override
    public @NotNull String getReasonDescription() {
        return "Entity is leashed to teleporting player";
    }

    @Override
    public @NotNull List<? extends Entity> getEntitiesToTeleport(@NotNull Player player) {
        return player.getNearbyEntities(12, 12, 12).stream().map(
                entity -> entity instanceof LivingEntity living ? living : null
        ).filter(Objects::nonNull).filter(
                living -> living.isLeashed() && player.equals(living.getLeashHolder())
        ).toList();
    }

    @Override
    public void beforeTeleport(@NotNull Player player, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) living.setLeashHolder(null);
    }

    @Override
    public void afterTeleport(@NotNull Player player, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) living.setLeashHolder(player);
    }
}