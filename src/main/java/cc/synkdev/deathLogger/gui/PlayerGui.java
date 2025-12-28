package cc.synkdev.deathLogger.gui;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.Util;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(UUID uuid, int page, boolean close) {
        List<Death> list = Util.getPlayersDeaths().get(uuid);
        OfflinePlayer OP = Bukkit.getOfflinePlayer(uuid);
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .title(Component.text(Lang.translate("playersDeath", core, OP.getName())))
                .rows(6)
                .create();

        if (close) {
            gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("close", core))).asGuiItem(event -> event.getWhoClicked().closeInventory()));
        } else {
            gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("back", core))).asGuiItem(event -> new PlayersGui().gui(1).open((Player) event.getWhoClicked())));
        }

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        if (list == null) {
            gui.setItem(3, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(ChatColor.RED+Lang.translate("noRecentDeath", core))).asGuiItem());
            return gui;
        }
        int totalPages = (list.size() + 45 - 1) / 45;
        if (page < totalPages) {
            gui.setItem(6, 6, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.translate("nextPage", core))).asGuiItem(event -> gui(uuid, page+1, close).open((Player) event.getWhoClicked())));
        }
        if (page > 1) {
            gui.setItem(6, 4, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.translate("prevPage", core))).asGuiItem(event -> gui(uuid, page-1, close).open(event.getWhoClicked())));
        }

        for (int i = (page-1)*45; i < page*45; i++) {
            if (list.size() <= i) break;
            Death d = list.get(i);
            gui.addItem(ItemBuilder.from(Material.SKELETON_SKULL).name(Component.text(Lang.translate("playerSpecDeath", core, OP.getName(), d.getId()+""))).asGuiItem(event -> new DeathGui().gui(d, false, (Player) event.getWhoClicked()).open(event.getWhoClicked())));

        }

        return gui;
    }
}
