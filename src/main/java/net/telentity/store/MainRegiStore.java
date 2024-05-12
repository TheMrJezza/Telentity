package net.telentity.store;

import net.telentity.api.TeHandle;
import net.telentity.api.TePrevent;
import net.telentity.api.registrable.RegiStore;
import net.telentity.api.registrable.Registrable;
import net.telentity.enforce.EntityEnforcer;
import net.telentity.enforce.chunk.ChunkCancelUnloadEnforcer;
import net.telentity.enforce.chunk.ChunkForceToggleEnforcer;
import net.telentity.enforce.chunk.ChunkTicketEnforcer;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

public class MainRegiStore implements RegiStore {
    private final PrStore prStore = new PrStore();
    private final TeStore teStore;
    private final EntityEnforcer entityEnforcer;
    private Registrable<Chunk> chunkEnforcer;

    public MainRegiStore(Plugin plugin) {
        teStore = new TeStore(plugin, prStore);
        entityEnforcer = new EntityEnforcer(plugin);
        final var sm = plugin.getServer().getServicesManager();
        sm.register(RegiStore.class, this, plugin, ServicePriority.Highest);

        try {
            chunkEnforcer = new ChunkTicketEnforcer(plugin);
        } catch (Throwable a) {
            try {
                chunkEnforcer = new ChunkForceToggleEnforcer(plugin);
            } catch (Throwable b) {
                chunkEnforcer = new ChunkCancelUnloadEnforcer(plugin);
            }
        }
    }

    @Override
    public @NotNull Registrable<TeHandle> getTeleportHandleStore() {
        return teStore;
    }

    @Override
    public @NotNull Registrable<TePrevent> getTeleportPreventorStore() {
        return prStore;
    }

    @Override
    public @NotNull Registrable<Chunk> getChunkEnforcer() {
        return chunkEnforcer;
    }

    @Override
    public @NotNull Registrable<Entity> getEntityEnforcer() {
        return entityEnforcer;
    }
}
