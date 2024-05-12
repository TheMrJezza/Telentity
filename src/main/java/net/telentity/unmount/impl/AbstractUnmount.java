package net.telentity.unmount.impl;

import net.telentity.api.tools.EntityTools;
import net.telentity.api.watcher.Unmount;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

public sealed abstract class AbstractUnmount implements Listener, Unmount
        permits ViaBukkitEntityDismountEvent, ViaSpigotEntityDismountEvent, ViaVehicleExitEvent {
    private final Plugin plugin;
    protected final PluginManager pm;
    private final HashMap<Player, Entity> vehicles = new HashMap<>();
    protected final EntityTools toolkit;

    protected AbstractUnmount(Plugin plugin, EntityTools toolkit) {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();
        this.toolkit = toolkit;
    }

    protected void setLastUnmount(Entity ejected, Entity vehicle) {
        if (!(ejected instanceof Player player) || vehicle == null) return;
        if (!toolkit.getEntityPassengerTools().isDriver(vehicle, player)) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> vehicles.remove(player));
        vehicles.put(player, vehicle);
    }

    @Override
    public final Entity lastUnmount(Player player) {
        return vehicles.get(player);
    }
}