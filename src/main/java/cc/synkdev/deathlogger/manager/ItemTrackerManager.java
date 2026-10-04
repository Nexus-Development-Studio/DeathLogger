package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.DeathItem;
import cc.synkdev.deathlogger.object.TrackerLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemTrackerManager {
    private ItemTrackerManager() {
        /* This utility class should not be instantiated */
    }

    private static final NamespacedKey DEATH_ID_KEY = new NamespacedKey(DeathLogger.getInstance(), "death_id");
    private static final NamespacedKey ITEM_UUID_KEY = new NamespacedKey(DeathLogger.getInstance(), "item_uuid");
    public static DeathItem getDeathItem(ItemStack item, int id, Location loc) {
        if (item != null && item.getType() != Material.AIR) {
            Util.log("Item good");
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return new DeathItem(item);
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            UUID uuid;
            if (pdc.has(ITEM_UUID_KEY, PersistentDataType.STRING)) {
                uuid = UUID.fromString(pdc.get(ITEM_UUID_KEY, PersistentDataType.STRING));
            } else {
                uuid = UUID.randomUUID();
                pdc.set(ITEM_UUID_KEY, PersistentDataType.STRING, uuid.toString());
            }
            Util.log("Item UUID: " + uuid.toString());
            int[] deathIds = pdc.getOrDefault(DEATH_ID_KEY, PersistentDataType.INTEGER_ARRAY, new int[0]);
            List<Integer> deathIdList = new ArrayList<>();
            for (int deathId : deathIds) {
                deathIdList.add(deathId);
            }
            deathIdList.add(id);
            pdc.set(DEATH_ID_KEY, PersistentDataType.INTEGER_ARRAY, deathIdList.stream().mapToInt(Integer::intValue).toArray());
            Util.log("Death IDs: " + deathIdList);
            item.setItemMeta(meta);
            return new DeathItem(uuid, item, TrackerLocation.DROP, loc, null);
        }
        return new DeathItem(item);
    }
    public static DeathItem[] convert(ItemStack[] items) {
        List<DeathItem> deathInvList = new ArrayList<>();
        for (ItemStack item : items) {
            deathInvList.add(new DeathItem(item));
        }
        return deathInvList.toArray(new DeathItem[0]);
    }
    public static DeathItem[] ofInventory(ItemStack[] contents, int id, Location loc) {
        List<DeathItem> deathInvList = new ArrayList<>();
        for (ItemStack item : contents) {
            deathInvList.add(getDeathItem(item, id, loc));
        }
        return deathInvList.toArray(new DeathItem[0]);
    }
    public static DeathItem[] clear(DeathItem[] inv) {
        for (DeathItem deathItem : inv) {
            boolean tracked = deathItem.getLocationType() != null && deathItem.getLocation() != null && deathItem.getUuid() != null;
            deathItem.setLocation(null);
            deathItem.setLocationType(null);
            deathItem.setUuid(null);
            if (tracked) {
                ItemStack is = deathItem.getItem();
                ItemMeta meta = is.getItemMeta();
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                pdc.remove(ITEM_UUID_KEY);
                pdc.remove(DEATH_ID_KEY);
                is.setItemMeta(meta);
                deathItem.setItem(is);
            }
        }
        return inv;
    }
    public static void update(ItemStack itemStack, TrackerLocation loc, Location location) {
        if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta() ||
                !itemStack.getItemMeta().getPersistentDataContainer().has(ITEM_UUID_KEY, PersistentDataType.STRING)) return;
        DeathItem item = getItem(UUID.fromString(itemStack.getItemMeta().getPersistentDataContainer().get(ITEM_UUID_KEY, PersistentDataType.STRING)));
        if (item != null) {
            item.setLocation(location);
            item.setLocationType(loc);
        }
    }
    public static DeathItem getItem(UUID uuid) {
        for (Death d : DeathLogger.getInstance().getDeaths()) {
            for (DeathItem item : d.getInv()) {
                if (item.getUuid() != null && item.getUuid().equals(uuid)) return item;
            }
        }
        return null;
    }
}
