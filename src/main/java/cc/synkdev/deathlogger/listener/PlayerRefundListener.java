package cc.synkdev.deathlogger.listener;

import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.manager.PlayerRefundManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerRefundListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (PlayerRefundManager.getRefundMap().containsKey(p.getUniqueId())) {
            PlayerRefundManager.getRefundMap().get(p.getUniqueId()).forEach(item -> {
                p.getInventory().remove(item);
                Util.sendMessage(p, "refundMessage", item.getAmount() + "x " + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()));
            });
            PlayerRefundManager.getRefundMap().remove(p.getUniqueId());
        }
    }
}
