package cc.synkdev.deathlogger.command;

import cc.synkdev.acf.BaseCommand;
import cc.synkdev.acf.annotation.CommandAlias;
import cc.synkdev.acf.annotation.CommandCompletion;
import cc.synkdev.acf.annotation.CommandPermission;
import cc.synkdev.acf.annotation.Default;
import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.gui.PlayerGui;
import cc.synkdev.deathlogger.gui.PlayersGui;
import cc.synkdev.nexusCore.bukkit.Lang;
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
