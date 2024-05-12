package net.telentity.api.registrable;

import net.telentity.api.TeHandle;
import net.telentity.api.TePrevent;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface RegiStore {
    @NotNull
    Registrable<TeHandle> getTeleportHandleStore();

    @NotNull
    Registrable<TePrevent> getTeleportPreventorStore();

    @NotNull
    Registrable<Chunk> getChunkEnforcer();

    @NotNull
    Registrable<Entity> getEntityEnforcer();
}