package net.telentity.store;

import net.telentity.api.TeHandle;
import net.telentity.api.TePrevent;
import net.telentity.api.registrable.Registrable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrStore extends Registrable<TePrevent> {
    public boolean isPrevented(
            @NotNull Player player, @NotNull Entity entity,
            @NotNull Location to, @NotNull TeHandle reason
    ) {
        return player.equals(entity) || registered.stream().anyMatch(
                pt -> pt.prevented(player, entity, to, reason)
        );
    }
}