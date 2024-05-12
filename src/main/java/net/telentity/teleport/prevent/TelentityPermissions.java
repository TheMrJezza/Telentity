package net.telentity.teleport.prevent;

import net.telentity.api.TeHandle;
import net.telentity.api.TePrevent;
import net.telentity.store.TeStore;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TelentityPermissions implements TePrevent {

    @Override
    public boolean prevented(
            @NotNull Player player, @NotNull Entity entity, @NotNull Location to, @NotNull TeHandle reason
    ) {
        if (player.isOp()) return false;
        final var reasonName = reason.getPermissionName();
        final var entityType = entity.getType().name().toLowerCase();
        final var teleportPermission = TeStore.PERM_PREFIX.formatted("%s.%s".formatted(reasonName, entityType));
        return !player.hasPermission(teleportPermission);
    }
}