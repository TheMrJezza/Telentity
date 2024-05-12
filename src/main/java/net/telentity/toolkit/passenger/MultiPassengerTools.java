package net.telentity.toolkit.passenger;

import net.telentity.api.tools.EntityPassengerTools;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class MultiPassengerTools implements EntityPassengerTools {

    static {
        try {
            Entity.class.getDeclaredMethod("getPassengers");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public boolean isDriver(Entity vehicle, Entity passenger) {
        return passenger.equals(vehicle.getPassengers().getFirst());
    }

    @Override public void addPassenger(Entity vehicle, Entity passenger) {
        if (vehicle == null || passenger == null) return;
        vehicle.addPassenger(passenger);
    }

    @Override public @NotNull Stream<Entity> getPassengers(Entity vehicle) {
        return vehicle.getPassengers().stream();
    }
}