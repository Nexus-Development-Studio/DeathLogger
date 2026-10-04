package cc.synkdev.deathlogger.object;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class RefundOption {
    public abstract String getName();
    public abstract String getDescription();
    public abstract void refund(Death d, Player p);
    public abstract Material getIcon();

}
