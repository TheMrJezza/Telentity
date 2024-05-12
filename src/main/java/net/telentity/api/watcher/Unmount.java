package net.telentity.api.watcher;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Unmount {
    Entity lastUnmount(Player player);
}