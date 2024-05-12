package net.telentity.unmount.impl;

import net.telentity.api.tools.EntityTools;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ViaSpigotEntityDismountEvent extends AbstractUnmount {

    private static final String EVENT_NAME = "org.spigotmc.event.entity.EntityDismountEvents";
    private static final MethodHandle GET_DISMOUNTED;
    private static final Class<? extends EntityEvent> CLASS;

    static {
        final var lookup = MethodHandles.lookup();
        try {
            CLASS = lookup.findClass(EVENT_NAME).asSubclass(EntityEvent.class);
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException e) {
            throw new RuntimeException(e);
        }

        try {
            GET_DISMOUNTED = lookup.findVirtual(CLASS, "getDismounted", MethodType.methodType(Entity.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Entity parseEvent(EntityEvent event) {
        try {
            return (Entity) GET_DISMOUNTED.invoke(event);
        } catch (Throwable e) {
            return null;
        }
    }

    public ViaSpigotEntityDismountEvent(Plugin plugin, EntityTools toolkit) {
        super(plugin, toolkit);
        pm.registerEvent(CLASS, this, EventPriority.MONITOR, (l, c) -> {
            if (c instanceof EntityEvent e) setLastUnmount(e.getEntity(), parseEvent(e));
        }, plugin, true);
    }
}