package net.telentity.enforce.chunk;

import net.telentity.api.registrable.Registrable;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

public class ChunkCancelUnloadEnforcer extends Registrable<Chunk> implements Listener {
    public ChunkCancelUnloadEnforcer(Plugin plugin) {
        final var pm = plugin.getServer().getPluginManager();
        pm.registerEvent(ChunkUnloadEvent.class, this, EventPriority.HIGHEST, (l, c) -> {
            if (!(c instanceof ChunkUnloadEvent ue && c instanceof Cancellable ce)) return;
            if (isRegistered(ue.getChunk())) ce.setCancelled(true);
        }, plugin, true);
    }
}
