package net.telentity.enforce;

import net.telentity.api.registrable.Registrable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.plugin.Plugin;

public class EntityEnforcer extends Registrable<Entity> implements Listener {

    public EntityEnforcer(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityLeash(PlayerLeashEntityEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamage(EntityDamageEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityUnLeash(PlayerUnleashEntityEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntitySpawn(EntitySpawnEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityTeleport(EntityTeleportEvent event) {
        if (isRegistered(event.getEntity())) event.setCancelled(false);
    }
}
