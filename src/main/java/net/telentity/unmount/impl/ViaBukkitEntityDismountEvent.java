package net.telentity.unmount.impl;

import net.telentity.api.tools.EntityTools;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.plugin.Plugin;

public final class ViaBukkitEntityDismountEvent extends AbstractUnmount {

    public ViaBukkitEntityDismountEvent(Plugin plugin, EntityTools toolkit) {
        super(plugin, toolkit);
        pm.registerEvent(EntityDismountEvent.class, this, EventPriority.MONITOR, (l, c) -> {
            if (c instanceof EntityDismountEvent e) setLastUnmount(e.getEntity(), e.getDismounted());
        }, plugin, true);
    }
}