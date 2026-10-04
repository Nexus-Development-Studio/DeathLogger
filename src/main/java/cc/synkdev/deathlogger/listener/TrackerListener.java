package cc.synkdev.deathlogger.listener;

import cc.synkdev.deathlogger.manager.ItemTrackerManager;
import cc.synkdev.deathlogger.object.TrackerLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class TrackerListener implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            ItemTrackerManager.update(event.getItem().getItemStack(), TrackerLocation.PLAYER, event.getEntity().getLocation());
        }
    }

}
