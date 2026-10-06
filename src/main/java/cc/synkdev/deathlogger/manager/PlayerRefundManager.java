package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerRefundManager {
    private PlayerRefundManager() {
        /* This utility class should not be instantiated */
    }

    @Getter private static final Map<UUID, List<ItemStack>> refundMap = new HashMap<>();
    public static void attemptRefund(UUID player, ItemStack item) {
        OfflinePlayer oP = Bukkit.getOfflinePlayer(player);
        if (oP.isOnline()) {
            assert oP.getPlayer() != null;
            oP.getPlayer().getInventory().remove(item);
            Util.sendMessage(oP.getPlayer(), "refundMessage", item.getAmount() + "x " + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()));
        } else {
            List<ItemStack> list = refundMap.getOrDefault(player, new ArrayList<>());
            list.add(item);
            refundMap.put(player, list);
        }
    }
}
