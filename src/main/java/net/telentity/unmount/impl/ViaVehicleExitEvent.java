package net.telentity.unmount.impl;

import net.telentity.api.tools.EntityTools;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;

public final class ViaVehicleExitEvent extends AbstractUnmount {

    public ViaVehicleExitEvent(Plugin plugin, EntityTools toolkit) {
        super(plugin, toolkit);
        pm.registerEvent(VehicleExitEvent.class, this, EventPriority.MONITOR, (l, c) -> {
            if (c instanceof VehicleExitEvent e) setLastUnmount(e.getExited(), e.getVehicle());
        }, plugin, true);
    }
}