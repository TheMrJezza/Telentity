package net.telentity.toolkit.passenger;

import net.telentity.api.tools.EntityPassengerTools;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public final class SinglePassengerTools implements EntityPassengerTools {

    @Override
    public boolean isDriver(Entity v, Entity p) {
        //noinspection deprecation
        return p != null && v != null && p.equals(v.getPassenger());
    }

    @Override
    public void addPassenger(Entity vehicle, Entity passenger) {
        if (vehicle == null || passenger == null) return;
        //noinspection deprecation
        vehicle.setPassenger(passenger);
    }

    @Override
    public @NotNull Stream<Entity> getPassengers(Entity vehicle) {
        //noinspection deprecation
        return Stream.of(vehicle.getPassenger()).filter(Objects::nonNull);
    }
}