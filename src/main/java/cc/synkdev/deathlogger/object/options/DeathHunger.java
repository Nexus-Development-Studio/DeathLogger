package cc.synkdev.deathlogger.object.options;

import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.RefundOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathHunger extends RefundOption {
    @Override
    public String getDescription() {
        return "deathHungerRefund";
    }

    @Override
    public String getName() {
        return "deathHunger";
    }

    @Override
    public void refund(Death d, Player p) {
        if (d.getHunger() == -1) return;
        p.setFoodLevel(d.getHunger());
        p.setSaturation(d.getSaturation());
    }

    @Override
    public Material getIcon() {
        return Material.COOKED_BEEF;
    }
}
