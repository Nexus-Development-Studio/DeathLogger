package cc.synkdev.deathlogger.gui;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.manager.ConfigManager;
import cc.synkdev.deathlogger.manager.integration.SkinUtils;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.kyori.adventure.text.Component;
import cc.synkdev.nexuscore.bukkit.Lang;
import cc.synkdev.triumph.builder.item.ItemBuilder;
import cc.synkdev.triumph.builder.item.SkullBuilder;
import cc.synkdev.triumph.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeathGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(Death d, boolean close, Player player) {
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(4)
                .title(Component.text(Lang.translate("deathGuiTitle", core, d.getId()+"")))
                .create();

        boolean self = d.getPlayer().equals(player.getUniqueId());
        boolean tp = self ? player.hasPermission("deathlogger.teleport.self") : player.hasPermission("deathlogger.teleport.all");
        boolean coords = self ? player.hasPermission("deathlogger.coordinates.self") : player.hasPermission("deathlogger.coordinates.all");
        boolean respawnTp = self ? player.hasPermission("deathlogger.respawn.teleport.self") : player.hasPermission("deathlogger.respawn.teleport.all");
        boolean respawnCoords = self ? player.hasPermission("deathlogger.respawn.coordinates.self") : player.hasPermission("deathlogger.respawn.coordinates.all");
        boolean inv = self ? player.hasPermission("deathlogger.inventory.view.self") : player.hasPermission("deathlogger.inventory.view.all");
        boolean last = self ? player.hasPermission("deathlogger.lastdeath.self") : player.hasPermission("deathlogger.lastdeath.all");
        boolean hasLast = Util.getPlayersDeaths().get(d.getPlayer()) != null && Util.getPlayersDeaths().get(d.getPlayer()).size() > 1;
        boolean hunger = self ? player.hasPermission("deathlogger.hunger.self") : player.hasPermission("deathlogger.hunger.all");
        boolean hasHunger = d.getHunger() != -1;
        boolean xp = self ? player.hasPermission("deathlogger.exp.self") : player.hasPermission("deathlogger.exp.all");
        boolean hasXp = d.getXp() != -1;
        boolean hasRefundPerm = player.hasPermission("deathlogger.refund");

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        OfflinePlayer oP = Bukkit.getOfflinePlayer(d.getPlayer());
        SkullBuilder builder;
        if (core.getSkinsRestorerAPI()!=null && ConfigManager.isUseSkinsRestorer()) {
            builder = ItemBuilder.skull().texture(core.skinUtils.getSkinValue(SkinUtils.getSkin(oP.getUniqueId(), oP.getName()).orElse(null)));
        } else {
            builder = ItemBuilder.skull().owner(oP);
        }
        gui.setItem(1, 5, builder.name(Component.text(ChatColor.AQUA+oP.getName())).asGuiItem());

        gui.setItem(2, 4, ItemBuilder.from(Material.CHEST).name(Component.text(Lang.translate("deathGuiInv", core))).asGuiItem(event -> {
            if (inv) {
                new DeathInventoryGui().gui(d, (Player) event.getWhoClicked()).open(event.getWhoClicked());
            } else {
                player.sendMessage(Lang.translate("noPermission", core));
            }
        }));
        gui.setItem(2, 2, ItemBuilder.from(Material.PAPER).name(Component.text(Lang.translate("deathMsg", core))).lore(Component.text(ChatColor.DARK_GRAY+d.getMsg())).asGuiItem());

        List<Component> lore = new ArrayList<>();
        if (!coords && !tp) lore.addAll(List.of(Component.text(""), Component.text(Util.translate("noPermMenu"))));
        else if (coords && !tp) lore.addAll(List.of(Component.text("  "+Lang.translate("deathLocLore1", core, d.getLoc().getWorld().getName())), Component.text("  "+Lang.translate("deathLocLore2", core, d.getLoc().getBlockX()+"", d.getLoc().getBlockY()+"", d.getLoc().getBlockZ()+""))));
        else if (!coords) lore.addAll(List.of(Component.text(""), Component.text(Lang.translate("deathLocLore3", core))));
        else lore.addAll(List.of(Component.text("  "+Lang.translate("deathLocLore1", core, d.getLoc().getWorld().getName())), Component.text("  "+Lang.translate("deathLocLore2", core, d.getLoc().getBlockX()+"", d.getLoc().getBlockY()+"", d.getLoc().getBlockZ()+"")),
                    Component.empty(), Component.text(Lang.translate("deathLocLore3", core))));

        gui.setItem(2, 6, ItemBuilder.from(Material.COMPASS)
                .name(Component.text(Lang.translate("deathLoc", core)))
                .lore(lore).asGuiItem(event -> {
                            if (tp) event.getWhoClicked().teleport(d.getLoc());
                }));

        if (d.getRespawnLoc() != null) {
            List<Component> loreRespawn = new ArrayList<>();
            if (!respawnCoords && !respawnTp) loreRespawn.addAll(List.of(Component.text(""), Component.text(Util.translate("noPermMenu"))));
            else if (respawnCoords && !respawnTp) loreRespawn.addAll(List.of(Component.text("  "+Lang.translate("respawnLocLore1", core, d.getRespawnLoc().getWorld().getName())), Component.text("  "+Lang.translate("respawnLocLore2", core, d.getRespawnLoc().getBlockX()+"", d.getRespawnLoc().getBlockY()+"", d.getRespawnLoc().getBlockZ()+""))));
            else if (!respawnCoords) loreRespawn.addAll(List.of(Component.text(""), Component.text(Lang.translate("respawnLocLore3", core))));
            else loreRespawn.addAll(List.of(Component.text("  "+Lang.translate("respawnLocLore1", core, d.getRespawnLoc().getWorld().getName())), Component.text("  "+Lang.translate("respawnLocLore2", core, d.getRespawnLoc().getBlockX()+"", d.getRespawnLoc().getBlockY()+"", d.getRespawnLoc().getBlockZ()+"")),
                        Component.empty(), Component.text(Lang.translate("respawnLocLore3", core))));

            Material compass;
            try {
                compass = Material.valueOf("RECOVERY_COMPASS");
            } catch (IllegalArgumentException _) {
                compass = Material.COMPASS;
            }
            gui.setItem(2, 7, ItemBuilder.from(compass)
                    .name(Component.text(Lang.translate("respawnLoc", core)))
                    .lore(loreRespawn).asGuiItem(event -> {
                        if (respawnTp) event.getWhoClicked().teleport(d.getRespawnLoc());
                    }));
        }

        gui.setItem(2, 8, ItemBuilder.from(Material.CLOCK).name(Component.text(Lang.translate("deathTime", core))).lore(Component.text("  "+ChatColor.GOLD+Util.formatUnixSeconds(d.getUnix()))).asGuiItem());

        if (hasLast) {
            if (last)
                gui.setItem(2, 3, ItemBuilder.from(Material.CLOCK).name(Component.text(Util.translate("deathTimeSinceLast"))).lore(Component.text("  " + ChatColor.GOLD + Util.formatDuration(d.getUnix() - Util.getPlayersDeaths().get(d.getPlayer()).stream().toList().get(Util.getPlayersDeaths().get(d.getPlayer()).size() - 2).getUnix()))).asGuiItem());
            else
                gui.setItem(2, 3, ItemBuilder.from(Material.CLOCK).name(Component.text(Lang.translate("deathTimeSinceLast", core))).lore(Component.text(""), Component.text(Util.translate("noPermMenu"))).asGuiItem());
        } else {
            gui.setItem(2, 3, ItemBuilder.from(Material.CLOCK).name(Component.text(Lang.translate("deathTimeSinceLast", core))).lore(Component.text(""), Component.text(Util.translate("noLastDeath", Util.getName(d.getPlayer())))).asGuiItem());
        }

        if (hasHunger) {
            if (hunger)
                gui.setItem(3, 4, ItemBuilder.from(Material.COOKED_BEEF).name(Component.text(Util.translate("deathHunger"))).lore(Component.text(""), Component.text("  " + Util.translate("deathHungerLore1", d.getHunger()+"")), Component.text("  "+Util.translate("deathHungerLore2", d.getSaturation()+""))).asGuiItem());
            else
                gui.setItem(3, 4, ItemBuilder.from(Material.COOKED_BEEF).name(Component.text(Lang.translate("deathHunger", core))).lore(Component.text(""), Component.text(Util.translate("noPermMenu"))).asGuiItem());
        }

        if (hasXp) {
            if (xp)
                gui.setItem(3, 6, ItemBuilder.from(Material.EXPERIENCE_BOTTLE).name(Component.text(Util.translate("deathXP"))).lore(Component.text(""), Component.text("  " + Util.translate("deathXPLore", d.getXp()+""))).asGuiItem());
            else
                gui.setItem(3, 6, ItemBuilder.from(Material.EXPERIENCE_BOTTLE).name(Component.text(Util.translate("deathXP"))).lore(Component.text(""), Component.text(Util.translate("noPermMenu"))).asGuiItem());
        }



        if (close) {
            gui.setItem(4, hasRefundPerm ? 6 : 5, ItemBuilder.from(Material.BARRIER)
                    .name(Component.text(Lang.translate("close", core))).asGuiItem(event -> event.getWhoClicked().closeInventory()));
        } else {
            gui.setItem(4, hasRefundPerm ? 6 : 5, ItemBuilder.from(Material.BARRIER)
                    .name(Component.text(Lang.translate("back", core))).asGuiItem(event -> new PlayerGui().gui(d.getPlayer(), 1, false).open(event.getWhoClicked())));
        }



        if (hasRefundPerm) {
            gui.setItem(4, 4, ItemBuilder.from(Material.BUCKET)
                    .name(Component.text(Util.translate("deathRefund")))
                    .lore(Component.empty(), Component.text(Util.translate("clickRefund", Util.getName(d.getPlayer()))))
                    .asGuiItem(event -> new RefundGui().gui(d, (Player) event.getWhoClicked()).open(event.getWhoClicked())));
        }

        return gui;
    }
}
