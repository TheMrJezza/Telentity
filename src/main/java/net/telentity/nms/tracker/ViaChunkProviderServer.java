package net.telentity.nms.tracker;

import net.telentity.nms.PlayerNMS;
import net.telentity.nms.WorldNMS;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedList;

public class ViaChunkProviderServer extends WorldNMS {

    public static final MethodHandle GET_CHUNK_PROVIDER;
    public static final MethodHandle GET_PLAYER_CHUNK_MAP;
    public static final MethodHandle GET_INT2OBJ_MAP;
    public static final MethodHandle GET_TRACKER_VIA_ENTITY_ID;
    public static final MethodHandle HIDE_ENTITY_FOR_PLAYER;
    public static final MethodHandle SHOW_ENTITY_FOR_PLAYER;

    public static final boolean SUPPORTED;

    static {
        try {
            final var int2ObjName = "it.unimi.dsi.fastutil.ints.Int2ObjectMap";
            Class<?> int2Obj;
            try {
                int2Obj = LOOKUP.findClass("org.bukkit.craftbukkit.libs." + int2ObjName);
            } catch (ClassNotFoundException ex) {
                int2Obj = LOOKUP.findClass(int2ObjName);
            }

            final var nms = "net.minecraft.server" + (NMS_TAGGED ? CRAFT_TAG : ".level.");
            final var chunkProvider = LOOKUP.findClass(nms + "ChunkProviderServer");
            final var chunkMap = LOOKUP.findClass(nms + "PlayerChunkMap");

            GET_CHUNK_PROVIDER = findMethodInClass(true, NMS_WORLD_CLASS, chunkProvider);
            GET_PLAYER_CHUNK_MAP = findGetterInClass(true, chunkProvider, chunkMap);
            GET_INT2OBJ_MAP = findGetterInClass(true, chunkMap, int2Obj);

            GET_TRACKER_VIA_ENTITY_ID = LOOKUP.unreflect(int2Obj.getMethod("get", int.class));
            final var methodList = new LinkedList<Method>();

            final var entityTrackerClass = LOOKUP.findClass(chunkMap.getName() + "$EntityTracker");
            for (final var method : entityTrackerClass.getDeclaredMethods()) {
                if (method.getReturnType() != void.class) continue;
                if (method.getParameterCount() != 1) continue;
                if (method.getParameterTypes()[0] != PlayerNMS.NMS_PLAYER_CLASS) continue;
                methodList.add(method);
            }

            if (methodList.size() != 2) throw new RuntimeException();
            methodList.sort(Comparator.comparingInt(a -> a.getName().charAt(0)));

            HIDE_ENTITY_FOR_PLAYER = LOOKUP.unreflect(methodList.getFirst());
            SHOW_ENTITY_FOR_PLAYER = LOOKUP.unreflect(methodList.getLast());

            SUPPORTED = true;
        } catch (IllegalAccessException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getChunkProvider(World world) throws Throwable {
        return GET_CHUNK_PROVIDER.invoke(getHandle(world));
    }

    public static Object getPlayerChunkMap(World world) throws Throwable {
        return GET_PLAYER_CHUNK_MAP.invoke(getChunkProvider(world));
    }

    public static Object getInt2ObjectMap(World world) throws Throwable {
        return GET_INT2OBJ_MAP.invoke(getPlayerChunkMap(world));
    }

    public static Object getTrackerViaEntityId(World world, int entityID) throws Throwable {
        return GET_TRACKER_VIA_ENTITY_ID.invoke(getInt2ObjectMap(world), entityID);
    }

    public static void hideEntityForPlayer(Player player, Entity entity) throws Throwable {
        HIDE_ENTITY_FOR_PLAYER.invoke(
                getTrackerViaEntityId(player.getWorld(), entity.getEntityId()), PlayerNMS.getHandle(player)
        );
    }

    public static void showEntityForPlayer(Player player, Entity entity) throws Throwable {
        SHOW_ENTITY_FOR_PLAYER.invoke(
                getTrackerViaEntityId(player.getWorld(), entity.getEntityId()), PlayerNMS.getHandle(player)
        );
    }
}