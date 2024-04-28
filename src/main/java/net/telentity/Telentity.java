package net.telentity;

import net.telentity.api.PreventionManager;
import net.telentity.api.TeleportReason;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Telentity extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        final PluginManager pm = getServer().getPluginManager();
        final BukkitScheduler sch = getServer().getScheduler();

        MiddleMan.INSTANCE.register((player, entity, destination, trigger) -> {
            // TODO Implement Permissions here?

            // TRUE means the teleport won't happen. FALSE means it will
            return false;
        });

        getServer().getServicesManager().register(
                PreventionManager.class, MiddleMan.INSTANCE, this, ServicePriority.Highest
        );

        // This cache must be emptied at the end of every tick. How you do that is up to you.
        final HashMap<Player, Entity> previousVehicleCache = new HashMap<>();

        // Listen for when entities stop being passengers of other entities.
        // We only care about this event if it is called before the PlayerTeleportEvent.
        // Usually for vanilla teleport mechanics, and poorly written plugins.
        pm.registerEvent(VersionUtil.EJECTION_LISTENER_CLASS, this, EventPriority.MONITOR, (l, e) -> {
            final Entity vehicle, ejected;
            if (e instanceof VehicleExitEvent) {
                vehicle = ((VehicleExitEvent) e).getVehicle();
                ejected = ((VehicleExitEvent) e).getExited();
            } else if (e instanceof EntityEvent) {
                ejected = ((EntityEvent) e).getEntity();
                if (null == (vehicle = VersionUtil.tryReadDismounted((EntityEvent) e))) return;
            } else return;

            // Do nothing if the player was already cached in the PlayerTeleportEvent
            if (!(ejected instanceof Player) || previousVehicleCache.containsKey(ejected)) return;

            // Make sure the player is the "main passenger", i.e. the one controlling the entity.
            // Always true on older MC versions since they do not support multiple passengers.
            if (!ejected.equals(vehicle.getPassenger())) return;

            previousVehicleCache.put((Player) ejected, vehicle);
            sch.runTask(this, () -> previousVehicleCache.remove(ejected));
        }, this, true);

        pm.registerEvent(PlayerTeleportEvent.class, this, EventPriority.MONITOR, (l, e) -> {

            // Ignore this event if it's called because of something irrelevant.
            final Location to = ((PlayerTeleportEvent) e).getTo();
            if (to == null) return;

            final Location from = ((PlayerTeleportEvent) e).getFrom();
            if (from == null) return;

            // using names here on purpose.
            switch (((PlayerTeleportEvent) e).getCause().name()) {
                // CraftBukkit 1.8.x (and potentially other versions) use UNKNOWN in places it shouldn't
                // Therefore, we also check the squared distance > 3.5 - This is 95% effective.
                case "UNKNOWN": if (VersionUtil.brokenTpCauses()) {
                    if (!to.getWorld().equals(from.getWorld()) || to.distanceSquared(from) > 3.5) break;
                }
                case "SPECTATE":
                case "DISMOUNT":
                case "EXIT_BED":
                case "MOUNT": // MOUNT doesn't exist, I'm trying to future-proof the plugin.
                    return;
            }

            final Player player = ((PlayerTeleportEvent) e).getPlayer();

            player.getNearbyEntities(12, 12, 12).stream().filter(entity -> {
                if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isLeashed()) return false;
                return player.equals(((LivingEntity) entity).getLeashHolder());
            }).map(entity -> (LivingEntity) entity).filter(living ->
                    !MiddleMan.INSTANCE.isPrevented(player, living, to, TeleportReason.LEASHED_TO_PLAYER)
            ).forEach(leashed -> {
                leashed.setLeashHolder(null);
                VersionUtil.visualBugPrevention(player, leashed);
                // Take note of the delay here... It's 3. Make this whatever you want,
                // but make sure it's HIGHER than the passenger delay below.
                antiInterpolatedTeleport(leashed, to, 3, () -> {
                    leashed.setLeashHolder(player);
                    VersionUtil.visualBugCorrection(player, leashed);
                });
            });

            final Entity vehicle = previousVehicleCache.computeIfAbsent(player, p -> {
                final Entity currentVehicle = p.getVehicle();
                sch.runTask(this, () -> previousVehicleCache.remove(p));
                if (currentVehicle == null || !p.equals(currentVehicle.getPassenger())) return null;
                return currentVehicle;
            });

            // Make sure we have a vehicle to teleport
            if (vehicle == null) return;
            if (MiddleMan.INSTANCE.isPrevented(player, vehicle, to, TeleportReason.VEHICLE_OF_PLAYER)) {
                return;
            }

            // check for passengers
            final Entity[] pass = {vehicle.getPassenger()};
            while (pass[0] != null) {
                if (!MiddleMan.INSTANCE.isPrevented(player, pass[0], to,
                        TeleportReason.PASSENGER_OF_PLAYERS_VEHICLE)) {
                    VersionUtil.visualBugPrevention(player, pass[0]);
                    // Take note of the delay here... It's 2. Make this whatever you want,
                    // but make sure it's HIGHER than the vehicle delay below.
                    antiInterpolatedTeleport(pass[0], to, 2, () -> {
                        VersionUtil.visualBugCorrection(player, vehicle);
                        VersionUtil.tryAddPassenger(vehicle, pass[0]);
                    });
                } else pass[0].leaveVehicle();
                pass[0] = vehicle.getPassenger();
            }

            VersionUtil.visualBugPrevention(player, vehicle);
            // Take note of the delay here... It's 1. Make this whatever you want,
            // but make sure it's LOWER than the other delays below.
            antiInterpolatedTeleport(vehicle, to, 1, () -> {
                VersionUtil.visualBugCorrection(player, vehicle);
                VersionUtil.tryAddPassenger(vehicle, player);
            });
        }, this, true);
    }

    private void antiInterpolatedTeleport(
            @NotNull final Entity entity, @NotNull final Location to,
            long delay, @NotNull Runnable complete
    ) {
        entity.eject();
        entity.teleport(to);
        if (!VersionUtil.nativeCorrectionsAvailable()) {
            if (to.getWorld().equals(entity.getWorld())) {
                final Location point = to.clone().subtract(0, .25, 0);
                final Entity proxy = to.getWorld().spawnEntity(point, EntityType.ARROW);
                proxy.teleport(point);
                VersionUtil.tryAddPassenger(proxy, entity);
                this.getServer().getScheduler().runTaskLater(this, () -> {
                    proxy.eject();
                    proxy.remove();
                    entity.teleport(to);
                }, 2);
                delay += 2;
            }
        }
        this.getServer().getScheduler().runTaskLater(this, complete, delay);
    }
}