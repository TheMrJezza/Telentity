package net.telentity.store;

import net.telentity.api.TeHandle;
import net.telentity.api.registrable.Registrable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeStore extends Registrable<TeHandle> {

    private final PrStore prStore;

    public static final String PERM_PREFIX = "telentity.teleport.%s";
    private static final PermissionDefault PERM_DEF = PermissionDefault.FALSE;
    private final Permission wildTeleportPerm;
    private final PluginManager pm;

    private final Set<Permission> reasonPerms = new HashSet<>();

    public TeStore(Plugin plugin, PrStore prStore) {
        this.prStore = prStore;
        this.pm = plugin.getServer().getPluginManager();
        wildTeleportPerm = new Permission(PERM_PREFIX.formatted("*.*"), PERM_DEF);
        pm.addPermission(wildTeleportPerm);
        for (final var type : EntityType.values()) {
            final var name = type.name().toLowerCase();
            final var wildReason = new Permission(PERM_PREFIX.formatted("*." + name), PERM_DEF);
            wildReason.addParent(wildTeleportPerm, true);
            pm.addPermission(wildReason);
            reasonPerms.add(wildReason);
        }
    }

    @Override
    public void register(TeHandle registration) {
        final var reason = registration.getPermissionName();
        final var reasonWild = new Permission(PERM_PREFIX.formatted(reason + ".*"), PERM_DEF);
        reasonWild.addParent(wildTeleportPerm, true);
        pm.addPermission(reasonWild);

        reasonPerms.forEach(perm -> {
            final var specificPerm = new Permission(
                    PERM_PREFIX.formatted(reason + "." + perm.getName().substring(PERM_PREFIX.length())), PERM_DEF
            );
            specificPerm.addParent(perm, true);
            pm.addPermission(specificPerm);
        });

        super.register(registration);
    }

    public @NotNull Map<Entity, Set<TeHandle>> collect(final Player player, final Location to) {
        final var result = new HashMap<Entity, Set<TeHandle>>();
        registered.forEach(handle -> handle.getEntitiesToTeleport(player).stream().filter(
                entity -> !prStore.isPrevented(player, entity, to, handle)
        ).forEach(entity -> result.computeIfAbsent(entity, k -> new HashSet<>()).add(handle)));
        return result;
    }
}