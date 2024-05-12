package net.telentity.teleport;

import net.telentity.api.registrable.RegiStore;
import net.telentity.api.tools.EntityShowHide;
import net.telentity.store.TeStore;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class PlayerTeleportListener implements Listener {

    private final Plugin plugin;
    private final RegiStore regiStore;
    private final EntityShowHide entityShowHide;

    public PlayerTeleportListener(Plugin plugin, RegiStore regiStore, EntityShowHide showHide) {
        this.plugin = plugin;
        this.regiStore = regiStore;
        this.entityShowHide = showHide;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        final var from = event.getFrom();
        final var to = event.getTo();

        if (to == null) return;
        final var fromWorld = from.getWorld();
        final var toWorld = to.getWorld();

        if (fromWorld == null || toWorld == null) return;
        final var sameWorld = fromWorld.equals(toWorld);
        switch (event.getCause()) {
            case DISMOUNT, SPECTATE, EXIT_BED -> {return;}
            case UNKNOWN -> {if (sameWorld && from.distanceSquared(to) <= 3.5) {return;}}
        }

        final var toChunk = toWorld.getChunkAt(to);
        regiStore.getChunkEnforcer().register(toChunk);

        final var player = event.getPlayer();
        final var scheduler = plugin.getServer().getScheduler();

        ((TeStore) regiStore.getTeleportHandleStore()).collect(player, to).forEach((entity, handlers) -> {
            final var chunk = entity.getWorld().getChunkAt(entity.getLocation());
            regiStore.getChunkEnforcer().register(chunk);
            handlers.forEach(handler -> handler.beforeTeleport(player, entity));
            beforeTeleport(player, entity, sameWorld);
            scheduler.runTask(plugin, () -> entity.teleport(to));
            scheduler.runTaskLater(plugin, () -> {
                handlers.forEach(handler -> handler.afterTeleport(player, entity));
                afterTeleport(player, entity, sameWorld);
                regiStore.getChunkEnforcer().unregister(chunk);
            }, 3);
        });
        scheduler.runTaskLater(plugin, () -> regiStore.getChunkEnforcer().unregister(toChunk), 4);
    }

    private void beforeTeleport(Player player, Entity entity, boolean refresh) {
        regiStore.getEntityEnforcer().register(entity);
        entity.eject();
        entity.leaveVehicle();
        entity.setFallDistance(-Float.MAX_VALUE);
        if (refresh) entityShowHide.hideEntity(player, entity);
    }

    private void afterTeleport(Player player, Entity entity, boolean refresh) {
        entity.setFallDistance(-Float.MAX_VALUE);
        regiStore.getEntityEnforcer().unregister(entity);
        if (refresh) entityShowHide.showEntity(player, entity);
    }
}