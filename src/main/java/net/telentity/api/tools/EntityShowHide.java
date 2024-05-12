package net.telentity.api.tools;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface EntityShowHide {
    void hideEntity(Player player, Entity entity);

    void showEntity(Player player, Entity entity);
}