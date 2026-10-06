package cc.synkdev.deathlogger.gui;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.manager.ConfigManager;
import cc.synkdev.deathlogger.manager.integration.SkinUtils;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.RefundOption;
import cc.synkdev.deathlogger.object.options.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RefundGui {
    private final DeathLogger core = DeathLogger.getInstance();
    public Gui gui(Death d, Player player, String... options) {
        Gui gui = Gui.gui()
                .disableAllInteractions()
                .rows(3)
                .title(Component.text(Lang.translate("refundGuiTitle", core, d.getId() + "")))
                .create();

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        OfflinePlayer oP = Bukkit.getOfflinePlayer(d.getPlayer());
        SkullBuilder builder;
        if (core.getSkinsRestorerAPI() != null && ConfigManager.isUseSkinsRestorer()) {
            builder = ItemBuilder.skull().texture(core.skinUtils.getSkinValue(SkinUtils.getSkin(oP.getUniqueId(), oP.getName()).orElse(null)));
        } else {
            builder = ItemBuilder.skull().owner(oP);
        }
        gui.setItem(1, 5, builder.name(Component.text(ChatColor.AQUA + oP.getName())).asGuiItem());

        Map<Integer, Class<? extends RefundOption>> refundOptions = Map.of(11, DeathHunger.class,
                12, DeathXp.class,
                13, DeathInv.class,
                14, DeathInvTake.class,
                15, DeathLocation.class);

        refundOptions.forEach((integer, aClass) -> {
            try {
                RefundOption refundOption = aClass.getDeclaredConstructor().newInstance();
                boolean selected = Arrays.stream(options).anyMatch(s -> s.equalsIgnoreCase(refundOption.getName()));
                gui.setItem(integer, ItemBuilder.from(refundOption.getIcon())
                        .name(Component.text(Util.translate(refundOption.getName())))
                        .glow(selected)
                        .lore(Component.text(""), Component.text(Util.translate(refundOption.getDescription())), Component.empty(), Component.text(Util.translate(selected ? "clickToggleOff" : "clickToggleOn")))
                        .asGuiItem(_ -> {
                            List<String> newOptions = new ArrayList<>(Arrays.asList(options));
                            if (selected) {
                                newOptions.remove(refundOption.getName());
                            } else {
                                newOptions.add(refundOption.getName());
                            }
                            gui(d, player, newOptions.toArray(new String[0])).open(player);
                        }));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        gui.setItem(3, 4, ItemBuilder.from(Material.LIME_WOOL).name(Component.text(Lang.translate("confirm", core))).asGuiItem(event -> {
            for (String option : options) {
                RefundOption rOp = Util.getOption(option);
                if (rOp != null) {
                    rOp.refund(d, Bukkit.getPlayer(d.getPlayer()));
                }
            }
            Util.sendMessage((Player) event.getWhoClicked(), "refundSuccess", Util.getName(d.getPlayer()), d.getId() + "");
            Util.sendMessage(Bukkit.getPlayer(d.getPlayer()), "refundReceived", Util.getName(event.getWhoClicked().getUniqueId()), d.getId() + "");
            event.getWhoClicked().closeInventory();
        }));
        gui.setItem(3, 6, ItemBuilder.from(Material.BARRIER).name(Component.text(Lang.translate("back", core))).asGuiItem(event -> new PlayerGui().gui(d.getPlayer(), 1, false).open(event.getWhoClicked())));


        return gui;
    }
}
