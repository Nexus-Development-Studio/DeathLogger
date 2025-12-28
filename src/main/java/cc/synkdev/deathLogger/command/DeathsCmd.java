package cc.synkdev.deathLogger.command;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.gui.PlayerGui;
import cc.synkdev.deathLogger.gui.PlayersGui;
import cc.synkdev.nexusCore.bukkit.Lang;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("deaths|death")
public class DeathsCmd extends BaseCommand {
    private final DeathLogger core = DeathLogger.getInstance();
    @Default
    @CommandPermission("deathlogger.players")
    @CommandCompletion("@players")
    public void onDefault(Player player, String[] args) {
        if (args.length == 0) {
            new PlayersGui().gui(1).open(player);
            return;
        }

        OfflinePlayer oP = Bukkit.getOfflinePlayer(args[0]);
        if (!oP.hasPlayedBefore() && !oP.isOnline()) {
            player.sendMessage(core.prefix()+ Lang.translate("noPlayer", core));
            return;
        }
        new PlayerGui().gui(oP.getUniqueId(), 1, true).open(player);
    }
}
