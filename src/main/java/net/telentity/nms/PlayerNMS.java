package net.telentity.nms;

import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;

public class PlayerNMS extends MinimumNMS {
    public static final Class<?> CRAFT_PLAYER_CLASS, NMS_PLAYER_CLASS;
    public static final MethodHandle CRAFT_PLAYER_GET_NMS_HANDLE;

    static {
        try {
            CRAFT_PLAYER_CLASS = LOOKUP.findClass(CRAFT_PACKAGE + ".entity.CraftPlayer");
            CRAFT_PLAYER_GET_NMS_HANDLE = LOOKUP.unreflect(CRAFT_PLAYER_CLASS.getDeclaredMethod("getHandle"));
            NMS_PLAYER_CLASS = CRAFT_PLAYER_GET_NMS_HANDLE.type().returnType();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getHandle(Player player) throws Throwable {
        return CRAFT_PLAYER_GET_NMS_HANDLE.invoke(player);
    }
}