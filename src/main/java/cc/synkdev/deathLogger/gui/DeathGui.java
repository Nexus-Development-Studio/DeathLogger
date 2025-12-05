package cc.synkdev.deathLogger.gui;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.Util;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.synkLibs.bukkit.Lang;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DeathGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(Death d, boolean close) {
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.translate("deathGuiTitle", core, d.getId()+"")))
                .create();

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        OfflinePlayer oP = Bukkit.getOfflinePlayer(d.getPlayer());
        gui.setItem(1, 5, ItemBuilder.skull().owner(oP).name(Component.text(ChatColor.AQUA+oP.getName())).asGuiItem());

        gui.setItem(2, 4, ItemBuilder.from(Material.CHEST).name(Component.text(Lang.translate("deathGuiInv", core))).asGuiItem(event -> new DeathInventoryGui().gui(d, (Player) event.getWhoClicked()).open(event.getWhoClicked())));
        gui.setItem(2, 2, ItemBuilder.from(Material.PAPER).name(Component.text(Lang.translate("deathMsg", core))).lore(Component.text(ChatColor.DARK_GRAY+d.getMsg())).asGuiItem());
        gui.setItem(2, 6, ItemBuilder.from(Material.COMPASS)
                .name(Component.text(Lang.translate("deathLoc", core)))
                .lore(Component.text("  "+Lang.translate("deathLocLore1", core, d.getLoc().getWorld().getName())), Component.text("  "+Lang.translate("deathLocLore2", core, d.getLoc().getBlockX()+"", d.getLoc().getBlockY()+"", d.getLoc().getBlockZ()+"")),
                        Component.empty(), Component.text(Lang.translate("deathLocLore3", core))).asGuiItem(event -> event.getWhoClicked().teleport(d.getLoc())));
        gui.setItem(2, 8, ItemBuilder.from(Material.CLOCK).name(Component.text(Lang.translate("deathTime", core))).lore(Component.text("  "+ChatColor.GOLD+Util.formatUnixSeconds(d.getUnix()))).asGuiItem());



        if (close) {
            gui.setItem(3, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("close", core))).asGuiItem(event -> event.getWhoClicked().closeInventory()));
        } else {
            gui.setItem(3, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("back", core))).asGuiItem(event -> new PlayerGui().gui(d.getPlayer(), 1, false).open((Player) event.getWhoClicked())));
        }


        return gui;
    }
}
