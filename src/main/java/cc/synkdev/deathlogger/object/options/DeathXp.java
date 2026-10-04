package cc.synkdev.deathlogger.object.options;

import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.RefundOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathXp extends RefundOption {
    @Override
    public String getDescription() {
        return "deathXPRefund";
    }

    @Override
    public String getName() {
        return "deathXP";
    }

    @Override
    public Material getIcon() {
        return Material.CHEST;
    }

    @Override
    public void refund(Death d, Player p) {
        if (d.getXp() == -1) return;
        p.giveExp(d.getXp());
    }
}
