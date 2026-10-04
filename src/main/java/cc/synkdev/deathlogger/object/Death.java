package cc.synkdev.deathlogger.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class Death {
    private int id;
    private UUID player;
    private Location loc;
    private Location respawnLoc;
    private String msg;
    private DeathItem[] inv;
    private long unix;
    private int hunger;
    private float saturation;
    private int xp;

    public boolean isSelf(OfflinePlayer player) {
        return this.player.equals(player.getUniqueId());
    }

    public ItemStack[] getInvItems() {
        ItemStack[] items = new ItemStack[inv.length];
        for (int i = 0; i < inv.length; i++) {
            items[i] = inv[i].getItem();
        }
        return items;
    }
}
