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
    private ItemStack[] inv;
    private long unix;

    public boolean isSelf(OfflinePlayer player) {
        return this.player.equals(player.getUniqueId());
    }
}
