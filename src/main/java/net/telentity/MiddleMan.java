package net.telentity;

import net.telentity.api.PreventionManager;
import net.telentity.api.Preventor;
import net.telentity.api.TeleportReason;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class MiddleMan implements PreventionManager {
    private MiddleMan() {}

    static final MiddleMan INSTANCE = new MiddleMan();

    private final HashSet<Preventor> preventions = new HashSet<>();

    boolean isPrevented(
            @NotNull final Player player, @NotNull final Entity entity,
            @NotNull final Location to, @NotNull final TeleportReason reason
    ) {
        if (player.equals(entity)) return true;
        return preventions.stream().anyMatch(pt -> pt.shouldPreventTeleport(player, entity, to, reason));
    }

    @Override public void register(Preventor preventor) {
        this.preventions.add(preventor);
    }

    @Override public void unregister(Preventor preventor) {
        this.preventions.remove(preventor);
    }

    @Override public boolean isRegistered(Preventor preventor) {
        return this.preventions.contains(preventor);
    }
}