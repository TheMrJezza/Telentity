package net.telentity.enforce.chunk;

import net.telentity.api.registrable.Registrable;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

public class ChunkTicketEnforcer extends Registrable<Chunk> {

    static {
        try {
            Chunk.class.getDeclaredMethod("getPluginChunkTickets");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Plugin plugin;

    public ChunkTicketEnforcer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Chunk chunk) {
        chunk.addPluginChunkTicket(plugin);
    }

    @Override
    public void unregister(Chunk chunk) {
        chunk.removePluginChunkTicket(plugin);
    }

    @Override
    public boolean isRegistered(Chunk chunk) {
        return chunk.getPluginChunkTickets().contains(plugin);
    }
}
