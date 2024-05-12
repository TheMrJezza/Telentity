package net.telentity.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TeHandle {
    @NotNull
    String getPermissionName();

    @NotNull
    String getReasonDescription();

    @NotNull
    List<? extends Entity> getEntitiesToTeleport(@NotNull Player player);

    void beforeTeleport(@NotNull Player player, @NotNull Entity entity);

    void afterTeleport(@NotNull Player player, @NotNull Entity entity);
}