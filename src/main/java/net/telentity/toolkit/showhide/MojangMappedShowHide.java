package net.telentity.toolkit.showhide;

import net.telentity.api.tools.EntityShowHide;
import net.telentity.nms.tracker.ViaChunkProviderServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MojangMappedShowHide implements EntityShowHide {

    static {
        try {
            if (!ViaChunkProviderServer.SUPPORTED) throw new RuntimeException();
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
            ViaChunkProviderServer.hideEntityForPlayer(player, entity);
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
            ViaChunkProviderServer.showEntityForPlayer(player, entity);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}