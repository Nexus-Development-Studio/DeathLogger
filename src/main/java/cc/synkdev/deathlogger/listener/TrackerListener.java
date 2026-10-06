package cc.synkdev.deathlogger.listener;

import cc.synkdev.deathlogger.Util;
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
            Util.log("TrackerListener: Player picked up item: " + event.getItem().getItemStack().getType().name());
            ItemTrackerManager.update(event.getItem().getItemStack(), TrackerLocation.PLAYER, event.getEntity().getUniqueId());
        }


    }

}
