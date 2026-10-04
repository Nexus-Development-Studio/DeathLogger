package cc.synkdev.deathlogger.object.options;

import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.RefundOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathLocation extends RefundOption {
    @Override
    public String getDescription() {
        return "deathLocRefund";
    }

    @Override
    public String getName() {
        return "deathLoc";
    }

    @Override
    public Material getIcon() {
        return Material.COMPASS;
    }

    @Override
    public void refund(Death d, Player p) {
        p.teleport(d.getLoc());
    }
}
