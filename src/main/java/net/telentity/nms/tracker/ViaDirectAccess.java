package net.telentity.nms.tracker;

import net.telentity.nms.PlayerNMS;
import net.telentity.nms.WorldNMS;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;

public class ViaDirectAccess extends WorldNMS {

    public static final MethodHandle GET_TRACKER, GET_TRACKED_ENTITIES, GET_ENTITY_TRACKER, HIDE_ENTITY, SHOW_ENTITY;
    public static final boolean SUPPORTED;

    static {
        try {
            GET_TRACKER = LOOKUP.unreflectGetter(NMS_WORLD_CLASS.getDeclaredField("tracker"));
            GET_TRACKED_ENTITIES = LOOKUP.unreflectGetter(
                    GET_TRACKER.type().returnType().getDeclaredField("trackedEntities")
            );
            GET_ENTITY_TRACKER = LOOKUP.unreflect(
                    GET_TRACKED_ENTITIES.type().returnType().getDeclaredMethod("get", int.class)
            );
            try {
                final var trackerEntry = LOOKUP.findClass("net.minecraft.server." + CRAFT_TAG + ".EntityTrackerEntry");
                HIDE_ENTITY = LOOKUP.unreflect(
                        trackerEntry.getDeclaredMethod("clear", PlayerNMS.NMS_PLAYER_CLASS)
                );
                SHOW_ENTITY = LOOKUP.unreflect(
                        trackerEntry.getDeclaredMethod("updatePlayer", PlayerNMS.NMS_PLAYER_CLASS)
                );
                SUPPORTED = true;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getWorldEntityTracker(World world) throws Throwable {
        return GET_TRACKER.invoke(getHandle(world));
    }

    public static Object getInt2ObjectMap(World world) throws Throwable {
        return GET_TRACKED_ENTITIES.invoke(getWorldEntityTracker(world));
    }

    public static Object getTrackerViaEntityId(World world, int entityID) throws Throwable {
        return GET_ENTITY_TRACKER.invoke(getInt2ObjectMap(world), entityID);
    }

    public static void hideEntityForPlayer(Player player, Entity entity) throws Throwable {
        HIDE_ENTITY.invoke(
                getTrackerViaEntityId(player.getWorld(), entity.getEntityId()), PlayerNMS.getHandle(player)
        );
    }

    public static void showEntityForPlayer(Player player, Entity entity) throws Throwable {
        SHOW_ENTITY.invoke(
                getTrackerViaEntityId(player.getWorld(), entity.getEntityId()), PlayerNMS.getHandle(player)
        );
    }
}