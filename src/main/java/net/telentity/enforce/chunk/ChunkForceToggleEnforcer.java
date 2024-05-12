package net.telentity.enforce.chunk;

import net.telentity.api.registrable.Registrable;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class ChunkForceToggleEnforcer extends Registrable<Chunk> {

    static {
        try {
            Chunk.class.getDeclaredMethod("isForceLoaded");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<Chunk> preForced = new HashSet<>();

    public ChunkForceToggleEnforcer(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            registered.forEach(chunk -> {
                if (chunk.isForceLoaded()) return;
                preForced.remove(chunk);
                chunk.setForceLoaded(true);
            });
        }, 0, 0);
    }

    @Override
    public void register(Chunk chunk) {
        if (!isRegistered(chunk) && chunk.isForceLoaded()) {
            preForced.add(chunk);
        }

        super.register(chunk);
        chunk.setForceLoaded(true);
    }

    @Override
    public void unregister(Chunk chunk) {
        chunk.setForceLoaded(preForced.contains(chunk));
        preForced.remove(chunk);
        super.unregister(chunk);
    }
}
