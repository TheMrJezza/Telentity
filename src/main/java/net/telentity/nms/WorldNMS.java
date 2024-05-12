package net.telentity.nms;

import org.bukkit.World;

import java.lang.invoke.MethodHandle;

public class WorldNMS extends MinimumNMS {
    public static final Class<?> CRAFT_WORLD_CLASS, NMS_WORLD_CLASS;
    public static final MethodHandle CRAFT_WORLD_GET_HANDLE;

    static {
        try {
            CRAFT_WORLD_CLASS = LOOKUP.findClass(CRAFT_PACKAGE + ".CraftWorld");
            CRAFT_WORLD_GET_HANDLE = LOOKUP.unreflect(CRAFT_WORLD_CLASS.getDeclaredMethod("getHandle"));
            NMS_WORLD_CLASS = CRAFT_WORLD_GET_HANDLE.type().returnType();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getHandle(World world) throws Throwable {
        return CRAFT_WORLD_GET_HANDLE.invoke(world);
    }
}