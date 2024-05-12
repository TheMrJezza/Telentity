package net.telentity.unmount;

import net.telentity.api.tools.EntityTools;
import net.telentity.api.watcher.Unmount;
import net.telentity.unmount.impl.ViaBukkitEntityDismountEvent;
import net.telentity.unmount.impl.ViaSpigotEntityDismountEvent;
import net.telentity.unmount.impl.ViaVehicleExitEvent;
import org.bukkit.plugin.Plugin;

public class UnmountResolver {
    private final Unmount unmount;

    public UnmountResolver(EntityTools entityTools, Plugin plugin) {
        Unmount available;
        try {
            available = new ViaBukkitEntityDismountEvent(plugin, entityTools);
        } catch (Throwable ignore) {
            try {
                available = new ViaSpigotEntityDismountEvent(plugin, entityTools);
            } catch (Throwable ignored) {
                available = new ViaVehicleExitEvent(plugin, entityTools);
            }
        }
        this.unmount = available;
    }

    public Unmount getUnmount() {
        return unmount;
    }
}