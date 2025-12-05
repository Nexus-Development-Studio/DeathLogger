package cc.synkdev.deathLogger.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class DeathItem {
    private int slot;
    private UUID uuid;
    private ItemStack item;
}
