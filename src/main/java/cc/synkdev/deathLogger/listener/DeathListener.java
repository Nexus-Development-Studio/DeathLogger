package cc.synkdev.deathLogger.listener;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.deathLogger.manager.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {
    private final DeathLogger core = DeathLogger.getInstance();
    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String message = event.getDeathMessage();
        ItemStack[] inv = p.getInventory().getContents();
        Death d = new Death(core.deaths.size(), p.getUniqueId(), p.getLocation(), message, inv, System.currentTimeMillis());
        FileManager.insert(d);
    }
}
