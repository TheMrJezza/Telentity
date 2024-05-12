package net.telentity.nms;

import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;

public class ConnectionNMS extends PlayerNMS {

    public static final Class<?> PACKET_CLASS, PLAYER_CONNECTION_CLASS;
    public static final MethodHandle NMS_PLAYER_GET_CONNECTION;
    public static final MethodHandle PLAYER_CONNECTION_SEND_PACKET;

    static {
        try {
            PACKET_CLASS = LOOKUP.findClass("net.minecraft.%s.Packet".formatted(
                    NMS_TAGGED ? "server." + CRAFT_TAG : "network.protocol"
            ));
            PLAYER_CONNECTION_CLASS = LOOKUP.findClass("net.minecraft.server.%s.PlayerConnection".formatted(
                    NMS_TAGGED ? CRAFT_TAG : "network"
            ));
            NMS_PLAYER_GET_CONNECTION = findGetterInClass(true, NMS_PLAYER_CLASS, PLAYER_CONNECTION_CLASS);
            PLAYER_CONNECTION_SEND_PACKET = findMethodInClass(false, PLAYER_CONNECTION_CLASS, void.class);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPlayerConnection(Player player) throws Throwable {
        return NMS_PLAYER_GET_CONNECTION.invoke(getHandle(player));
    }

    public static void sendPacket(Player player, Object packet) throws Throwable {
        PLAYER_CONNECTION_SEND_PACKET.invoke(getPlayerConnection(player), packet);
    }
}