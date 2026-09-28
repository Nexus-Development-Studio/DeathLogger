package cc.synkdev.deathlogger.command;

import cc.synkdev.acf.BaseCommand;
import cc.synkdev.acf.annotation.CommandAlias;
import cc.synkdev.acf.annotation.CommandCompletion;
import cc.synkdev.acf.annotation.CommandPermission;
import cc.synkdev.acf.annotation.Subcommand;
import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.gui.DeathInventoryGui;
import cc.synkdev.deathlogger.gui.PlayerGui;
import cc.synkdev.deathlogger.gui.PlayersGui;
import cc.synkdev.deathlogger.manager.ConfigManager;
import cc.synkdev.deathlogger.manager.FileManager;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import cc.synkdev.nexusCore.bukkit.NexusUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@CommandAlias("deathlogger|dl|deathl|dlogger")
public class MainCommand extends BaseCommand {
    private final DeathLogger core = DeathLogger.getInstance();

    @Subcommand("players")
    @CommandPermission("deathlogger.players")
    public void onPlayers(Player player) {
        new PlayersGui().gui(1).open(player);
    }

    @Subcommand("player")
    @CommandPermission("deathlogger.player")
    @CommandCompletion("@players")
    public void onPlayer(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(core.prefix()+Lang.translate("noArgPlayer", core));
            return;
        }

        OfflinePlayer oP = Bukkit.getOfflinePlayer(args[0]);
        if (oP == null || (!oP.hasPlayedBefore() && !oP.isOnline())) {
            p.sendMessage(core.prefix()+Lang.translate("noPlayer", core));
            return;
        }

        new PlayerGui().gui(oP.getUniqueId(), 1, true).open(p);
    }

    @Subcommand("reload")
    @CommandPermission("deathlogger.reload")
    public void onReload(CommandSender sender) {
        ConfigManager.init(core);
        NexusUtils.initLang(core, core.langMap, ConfigManager.getLang());
        FileManager.read();
        sender.sendMessage(ChatColor.GREEN + Lang.translate("reloaded", core));
    }

    @Subcommand("inv|inventory")
    public void onInv(Player p, String[] args) {
        if (args.length < 1) {
            Util.sendMessage(p, "invUsage");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Util.sendMessage(p, "invUsage");
            return;
        }
        Death death = Util.getDeath(id);
        if (death == null) {
            Util.sendMessage(p, "noDeath");
            return;
        }

        if (!p.hasPermission("deathlogger.inventory.view."+(death.isSelf(p) ? "self" : "all"))) {
            Util.sendMessage(p, "noPermission");
            return;
        }
        new DeathInventoryGui().gui(death, p).open(p);
    }
}
