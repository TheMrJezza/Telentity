package net.telentity.toolkit.showhide;

import net.telentity.api.tools.EntityShowHide;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitShowHideApi implements EntityShowHide {

    private final Plugin plugin;

    public BukkitShowHideApi(Plugin plugin) {
        this.plugin = plugin;
    }

    static {
        try {
            Player.class.getDeclaredMethod("showEntity", Plugin.class, Entity.class);
            Player.class.getDeclaredMethod("hideEntity", Plugin.class, Entity.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void hideEntity(Player player, Entity entity) {
        if (player == null || entity == null) return;
        player.hideEntity(plugin, entity);
    }

    @Override
    public void showEntity(Player player, Entity entity) {
        if (player == null || entity == null) return;
        player.showEntity(plugin, entity);
    }
}
