package net.telentity.nms;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

public class MinimumNMS {
    protected static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    protected static final String CRAFT_PACKAGE, CRAFT_TAG;
    protected static final boolean NMS_TAGGED;

    static {
        CRAFT_PACKAGE = Bukkit.getServer().getClass().getPackageName();
        CRAFT_TAG = CRAFT_PACKAGE.split("\\.")[3];
        var isTagged = true;
        try {
            LOOKUP.findClass("net.minecraft.server." + CRAFT_TAG + ".Entity");
        } catch (ClassNotFoundException | IllegalAccessException e) {
            isTagged = false;
        }
        NMS_TAGGED = isTagged;
    }

    protected static MethodHandle findGetterInClass(
            boolean isDeclared, @NotNull Class<?> parentClass, @NotNull Class<?> returnType
    ) throws IllegalAccessException, NoSuchFieldException {
        for (final var field : isDeclared ? parentClass.getDeclaredFields() : parentClass.getFields()) {
            if (field.getType() != returnType) continue;
            return LOOKUP.unreflectGetter(field);
        }
        throw new NoSuchFieldException();
    }

    protected static MethodHandle findMethodInClass(
            boolean isDeclared, @NotNull Class<?> parentClass, @NotNull Class<?> returnType, Class<?>... parameterTypes
    ) throws IllegalAccessException, NoSuchMethodException {
        for (final var method : isDeclared ? parentClass.getDeclaredMethods() : parentClass.getMethods()) {
            if (method.getReturnType() != returnType) continue;
            if (!Arrays.equals(parameterTypes, method.getParameterTypes())) continue;
            return LOOKUP.unreflect(method);
        }
        throw new NoSuchMethodException();
    }
}