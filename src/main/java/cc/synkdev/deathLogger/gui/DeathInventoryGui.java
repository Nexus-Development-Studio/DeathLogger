package cc.synkdev.deathLogger.gui;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import cc.synkdev.triumph.builder.item.ItemBuilder;
import cc.synkdev.triumph.guis.Gui;
import cc.synkdev.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeathInventoryGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(Death d, Player p) {
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(Component.text(Lang.translate("deathInvGuiTitle", core, d.getId()+"")))
                .create();

        gui.getFiller().fillBetweenPoints(1, 1, 5, 9, ItemBuilder.from(Material.WHITE_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        int index = 0;
        boolean take = p.hasPermission("deathlogger.inventory.take");

        for (ItemStack item : d.getInv()) {
            if (item != null) {
                gui.setItem(index, ItemBuilder.from(item).asGuiItem(event -> {
                    if (take) event.getWhoClicked().getInventory().addItem(item);
                }));
            }
            index++;
        }

        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("back", core))).asGuiItem(event -> new DeathGui().gui(d, false, (Player) event.getWhoClicked()).open(p)));
        gui.setItem(6, 3, ItemBuilder.from(Material.CHEST)
                .name(Component.text(Lang.translate("giveYourself", core)))
                        .lore(Component.text(Lang.translate("giveWarn", core, p.getName())))
                .asGuiItem(event -> {
                    if (event.getWhoClicked().hasPermission("deathlogger.inventory.take")) {
                        event.getWhoClicked().getInventory().setContents(d.getInv());
                    } else {
                        event.getWhoClicked().sendMessage(Lang.translate("noPermission", core));
                    }
                }));
        gui.setItem(6, 7, ItemBuilder.from(Material.CHEST).name(Component.text(Lang.translate("giveInv", core)))
                .lore(Component.text(Lang.translate("giveWarn", core, Bukkit.getOfflinePlayer(d.getPlayer()).getName()))).asGuiItem(event -> {
                    if (event.getWhoClicked().hasPermission("deathlogger.inventory.take")) {
                        OfflinePlayer oP = Bukkit.getOfflinePlayer(d.getPlayer());
                        if (oP.isOnline()) {
                            oP.getPlayer().getInventory().setContents(d.getInv());
                        } else {
                            event.getWhoClicked().sendMessage(core.prefix() + Lang.translate("offline", core));
                        }
                    } else {
                        event.getWhoClicked().sendMessage(Lang.translate("noPermission", core));
                    }
        }));
        return gui;
    }
}
