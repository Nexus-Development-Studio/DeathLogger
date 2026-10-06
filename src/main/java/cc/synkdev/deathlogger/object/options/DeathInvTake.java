package cc.synkdev.deathlogger.object.options;

import cc.synkdev.deathlogger.manager.ItemTrackerManager;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.DeathItem;
import cc.synkdev.deathlogger.object.RefundOption;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DeathInvTake extends RefundOption {
    @Override
    public String getDescription() {
        return "deathGuiInvTakeRefundLore";
    }

    @Override
    public String getName() {
        return "deathGuiInvTakeRefund";
    }

    @Override
    public Material getIcon() {
        return Material.ENDER_CHEST;
    }

    @Override
    public void refund(Death d, Player p) {
        for (DeathItem dItem : d.getInv()) {
            if (dItem.getLocationType() == null) continue;
            dItem.getLocationType().refund.accept(dItem);
        }
    }
}
