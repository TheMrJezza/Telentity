package net.telentity.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TePrevent {
    boolean prevented(
            @NotNull final Player player, @NotNull final Entity entity,
            @NotNull final Location to, @NotNull final TeHandle reason
    );
}