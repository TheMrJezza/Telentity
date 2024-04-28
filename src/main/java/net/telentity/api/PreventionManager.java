package net.telentity.api;

public interface PreventionManager {
    void register(Preventor preventor);
    void unregister(Preventor preventor);
    boolean isRegistered(Preventor preventor);
}