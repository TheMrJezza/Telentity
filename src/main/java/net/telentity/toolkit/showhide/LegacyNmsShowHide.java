package net.telentity.toolkit.showhide;

import net.telentity.api.tools.EntityShowHide;
import net.telentity.nms.tracker.ViaDirectAccess;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LegacyNmsShowHide implements EntityShowHide {

    static {
        try {
            if (!ViaDirectAccess.SUPPORTED) throw new RuntimeException();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void hideEntity(Player player, Entity entity) {
        if (player == null || entity == null) return;
        if (entity instanceof Player target) {
            player.hidePlayer(target);
        } else try {
            ViaDirectAccess.hideEntityForPlayer(player, entity);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void showEntity(Player player, Entity entity) {
        if (player == null || entity == null) return;
        if (entity instanceof Player target) {
            player.showPlayer(target);
        } else try {
            ViaDirectAccess.showEntityForPlayer(player, entity);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}