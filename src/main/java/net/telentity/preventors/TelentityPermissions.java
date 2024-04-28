package net.telentity.preventors;

import net.telentity.api.PreventionManager;
import net.telentity.api.Preventor;
import net.telentity.api.TeleportReason;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class TelentityPermissions implements Preventor {

    private final Plugin plugin;

    public TelentityPermissions(Plugin plugin) {
        this.plugin = plugin;
        RegisteredServiceProvider<PreventionManager> pm;
        pm = plugin.getServer().getServicesManager().getRegistration(PreventionManager.class);
        if (pm == null || pm.getProvider() == null) return;

        buildPermissions(plugin);

        PreventionManager manager = pm.getProvider();
        manager.register(this);
    }

    private void buildPermissions(Plugin plugin) {
        final PluginManager pm = plugin.getServer().getPluginManager();
        for (EntityType entityType : EntityType.values()) {

            final String type = entityType.name().toLowerCase();
            final Permission parentPerm = new Permission("telentity.teleport." + type, PermissionDefault.OP);
            pm.addPermission(parentPerm);

            for (TeleportReason reason : TeleportReason.values()) {
                final Permission subPerm = new Permission(
                        getRequiredPermission(entityType, reason),
                        PermissionDefault.OP
                );
                subPerm.addParent(parentPerm, true);
                pm.addPermission(subPerm);
            }
        }
    }

    private String getRequiredPermission(EntityType entityType, TeleportReason reason) {
        return "telentity." + reason.name().toLowerCase().split("_")[0].toLowerCase()
               + "." + entityType.name().toLowerCase();
    }

    @Override
    public boolean shouldPreventTeleport(
            @NotNull Player player, @NotNull Entity entity,
            @NotNull Location to, @NotNull TeleportReason reason
    ) {
        // If the player is OP. This check isn't actually needed, but we'll keep it.
        if (player.isOp()) return false;

        // Does the player have the appropriate permission(s) to teleport this entity?
        final String requiredPermission = getRequiredPermission(entity.getType(), reason);
        if (player.hasPermission(requiredPermission)) return false;
        plugin.getLogger().info(
                "[TelentityPermissions] Teleportation was prevented:" +
                "\nPrevented Because: Player lacks required permission" +
                "\nRequired Permission: " + requiredPermission +
                "\nPlayer: " + player.getName() +
                "\nEntity: " + entity.getType() +
                "\nTeleportReason: " + reason.name() +
                "\nLocation: " + to
        );
        return true;
    }
}