package net.telentity.api.tools;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface EntityPassengerTools {
    boolean isDriver(Entity vehicle, Entity passenger);

    void addPassenger(Entity vehicle, Entity passenger);

    @NotNull
    Stream<? extends Entity> getPassengers(Entity vehicle);
}
