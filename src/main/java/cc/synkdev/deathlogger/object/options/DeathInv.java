package cc.synkdev.deathlogger.object.options;

import cc.synkdev.deathlogger.manager.ItemTrackerManager;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.RefundOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathInv extends RefundOption {
    @Override
    public String getDescription() {
        return "deathGuiInvRefund";
    }

    @Override
    public String getName() {
        return "deathGuiInv";
    }

    @Override
    public Material getIcon() {
        return Material.CHEST;
    }

    @Override
    public void refund(Death d, Player p) {
        d.setInv(ItemTrackerManager.clear(d.getInv()));
        p.getInventory().setContents(d.getInvItems());
    }
}
