package net.telentity.toolkit;

import net.telentity.api.tools.EntityPassengerTools;
import net.telentity.api.tools.EntityShowHide;
import net.telentity.api.tools.EntityTools;
import net.telentity.toolkit.passenger.MultiPassengerTools;
import net.telentity.toolkit.passenger.SinglePassengerTools;
import net.telentity.toolkit.showhide.BukkitShowHideApi;
import net.telentity.toolkit.showhide.LegacyNmsShowHide;
import net.telentity.toolkit.showhide.MojangMappedShowHide;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class MainEntityTools implements EntityTools {

    @NotNull
    private EntityPassengerTools passengerTools;

    @NotNull
    private EntityShowHide showHide;

    public MainEntityTools(Plugin plugin) {
        try {
            passengerTools = new MultiPassengerTools();
        } catch (Throwable e) {
            passengerTools = new SinglePassengerTools();
        }

        try {
            showHide = new BukkitShowHideApi(plugin);
        } catch (Throwable e) {
            try {
                showHide = new MojangMappedShowHide();
            } catch (Throwable t) {
                showHide = new LegacyNmsShowHide();
            }
        }
    }

    @Override
    public @NotNull EntityShowHide getEntityShowHide() {
        return showHide;
    }

    @Override
    public @NotNull EntityPassengerTools getEntityPassengerTools() {
        return passengerTools;
    }
}