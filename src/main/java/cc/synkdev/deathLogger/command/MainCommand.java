package cc.synkdev.deathLogger.command;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.gui.PlayerGui;
import cc.synkdev.deathLogger.gui.PlayersGui;
import cc.synkdev.deathLogger.manager.FileManager;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
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
        core.reloadConfig();
        core.langMap.clear();
        core.langMap.putAll(Lang.init(core, new File(core.getDataFolder(), "lang.json"), core.lang));
        FileManager.read();
        sender.sendMessage(ChatColor.GREEN + Lang.translate("reloaded", core));
    }
}
