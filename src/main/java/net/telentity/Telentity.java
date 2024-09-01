package net.telentity;

import net.telentity.api.registrable.RegiStore;
import net.telentity.api.tools.EntityTools;
import net.telentity.store.MainRegiStore;
import net.telentity.teleport.PlayerTeleportListener;
import net.telentity.teleport.handler.LeashTeleportHandle;
import net.telentity.teleport.handler.NearbySittableTeleportHandle;
import net.telentity.teleport.handler.VehiclePassengerTeleportHandle;
import net.telentity.teleport.handler.VehicleTeleportHandle;
import net.telentity.teleport.prevent.TelentityPermissions;
import net.telentity.toolkit.MainEntityTools;
import net.telentity.unmount.UnmountResolver;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Telentity extends JavaPlugin {
    public void onEnable() {
        final var regiStore = new MainRegiStore(this);
        final var entityTools = new MainEntityTools(this);
        final var unmount = new UnmountResolver(entityTools, this).getUnmount();
        final var vehicleHandle = new VehicleTeleportHandle(unmount, entityTools);
        final var passengerHandle = new VehiclePassengerTeleportHandle(vehicleHandle, entityTools);

        regiStore.getTeleportHandleStore().register(vehicleHandle);
        regiStore.getTeleportHandleStore().register(passengerHandle);
        regiStore.getTeleportHandleStore().register(new LeashTeleportHandle());
        regiStore.getTeleportHandleStore().register(new NearbySittableTeleportHandle());
        regiStore.getTeleportPreventorStore().register(new TelentityPermissions());

        final var rsp = getServer().getServicesManager();
        rsp.register(RegiStore.class, regiStore, this, ServicePriority.Highest);
        rsp.register(EntityTools.class, entityTools, this, ServicePriority.Highest);

        new PlayerTeleportListener(this, regiStore, entityTools.getEntityShowHide());
    }
}
