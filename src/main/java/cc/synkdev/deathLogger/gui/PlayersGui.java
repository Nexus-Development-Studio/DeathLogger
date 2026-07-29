package cc.synkdev.deathLogger.gui;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.Util;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import cc.synkdev.triumph.builder.item.ItemBuilder;
import cc.synkdev.triumph.builder.item.SkullBuilder;
import cc.synkdev.triumph.guis.Gui;
import cc.synkdev.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayersGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(int page) {
        Map<UUID, List<Death>> map = Util.getPlayersDeaths();
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .title(Component.text(Lang.translate("playersMenuTitle", core)))
                .rows(6)
                .create();

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        int totalPages = (map.size() + 45 - 1) / 45;
        if (page < totalPages) {
            gui.setItem(6, 6, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.translate("nextPage", core))).asGuiItem(event -> gui(page+1).open(event.getWhoClicked())));
        }
        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("close", core))).asGuiItem(event -> event.getWhoClicked().closeInventory()));
        if (page > 1) {
            gui.setItem(6, 4, ItemBuilder.from(Material.ARROW).name(Component.text(Lang.translate("prevPage", core))).asGuiItem(event -> gui(page-1).open(event.getWhoClicked())));
        }

        for (int i = (page-1)*45; i < page*45; i++) {
            if (map.size() <= i) break;
            UUID uuid = new ArrayList<>(map.keySet()).get(i);
            OfflinePlayer OP = Bukkit.getOfflinePlayer(uuid);
            Util.debug("Name: "+OP.getName());
            SkullBuilder builder;
            if (core.getSkinsRestorerAPI()!=null && OP.isOnline()) {
                builder = ItemBuilder.skull().texture(core.skinUtils.getSkinValue(core.skinUtils.getPlayerSkin(OP.getPlayer())));
            } else {
                builder = ItemBuilder.skull().owner(OP);
            }
            gui.addItem(builder.name(Component.text(Lang.translate("playersDeath", core, OP.getName()))).asGuiItem(event -> new PlayerGui().gui(uuid, 1, false).open(event.getWhoClicked())));

        }

        return gui;
    }
}
