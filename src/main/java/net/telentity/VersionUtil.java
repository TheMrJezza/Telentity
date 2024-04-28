package net.telentity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("JavaReflectionMemberAccess")
public class VersionUtil {
    private static final Method ENTITY_ADD_PASSENGER_METHOD, ENTITY_DISMOUNT_EVENT_GET_DISMOUNTED_METHOD;
    private static final Method PLAYER_HIDE_ENTITY_METHOD, PLAYER_SHOW_ENTITY_METHOD;
    private static final String[] DISMOUNT_EVENT_CLASSES = {
            "org.spigotmc.event.entity.EntityDismountEvent",
            "org.bukkit.event.entity.EntityDismountEvent",
    };
    public static final Class<? extends Event> EJECTION_LISTENER_CLASS;

    static {
        Class<? extends Event> tempClass = VehicleExitEvent.class;
        for (String clazz : DISMOUNT_EVENT_CLASSES) {
            try {
                tempClass = Class.forName(clazz).asSubclass(Event.class);
            } catch (ClassNotFoundException | ClassCastException ignored) {}
        }
        EJECTION_LISTENER_CLASS = tempClass;
        Method tempMethod;
        try {
            tempMethod = Entity.class.getDeclaredMethod("addPassenger", Entity.class);
        } catch (NoSuchMethodException ignored) {tempMethod = null;}
        ENTITY_ADD_PASSENGER_METHOD = tempMethod;
        try {
            tempMethod = EJECTION_LISTENER_CLASS.getDeclaredMethod("getDismounted");
        } catch (NoSuchMethodException e) {tempMethod = null;}
        ENTITY_DISMOUNT_EVENT_GET_DISMOUNTED_METHOD = tempMethod;
        try {
            tempMethod = Player.class.getDeclaredMethod("hideEntity", Plugin.class, Entity.class);
        } catch (NoSuchMethodException e) {tempMethod = null;}
        PLAYER_HIDE_ENTITY_METHOD = tempMethod;
        try {
            tempMethod = Player.class.getDeclaredMethod("showEntity", Plugin.class, Entity.class);
        } catch (NoSuchMethodException e) {tempMethod = null;}
        PLAYER_SHOW_ENTITY_METHOD = tempMethod;
    }

    public static boolean brokenTpCauses() {
        // TODO Figure out which versions of CraftBukkit incorrectly use TeleportCause.UNKNOWN
        // Returning true for all versions is fine until then. Likely no one will notice.
        return true;
    }

    public static void tryAddPassenger(Entity vehicle, Entity passenger) {
        if (ENTITY_ADD_PASSENGER_METHOD == null) vehicle.setPassenger(passenger);
        else try {
            ENTITY_ADD_PASSENGER_METHOD.invoke(vehicle, passenger);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    public static Entity tryReadDismounted(EntityEvent event) {
        if (ENTITY_DISMOUNT_EVENT_GET_DISMOUNTED_METHOD != null) try {
            return (Entity) ENTITY_DISMOUNT_EVENT_GET_DISMOUNTED_METHOD.invoke(event);
        } catch (InvocationTargetException | IllegalAccessException ignored) {}
        return null;
    }

    public static boolean nativeCorrectionsAvailable() {
        return PLAYER_HIDE_ENTITY_METHOD != null && PLAYER_SHOW_ENTITY_METHOD != null;
    }

    public static void visualBugCorrection(Player player, Entity entity) {
        if (PLAYER_SHOW_ENTITY_METHOD != null) try {
            PLAYER_SHOW_ENTITY_METHOD.invoke(player, JavaPlugin.getPlugin(Telentity.class), entity);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    public static void visualBugPrevention(Player player, Entity entity) {
        if (PLAYER_HIDE_ENTITY_METHOD != null) try {
            PLAYER_HIDE_ENTITY_METHOD.invoke(player, JavaPlugin.getPlugin(Telentity.class), entity);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }
}