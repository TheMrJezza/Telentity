package net.telentity.api.registrable;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Registrable<T> {
    protected final Set<T> registered = new LinkedHashSet<>();

    public void register(T registration) {
        if (registered.contains(registration)) return;
        registered.add(registration);
    }

    public void unregister(T registration) {
        registered.remove(registration);
    }

    public boolean isRegistered(T registration) {
        return registered.contains(registration);
    }
}