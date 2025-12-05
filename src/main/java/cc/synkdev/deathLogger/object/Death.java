package cc.synkdev.deathLogger.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class Death {
    private int id;
    private UUID player;
    private Location loc;
    private String msg;
    private ItemStack[] inv;
    private long unix;
}
