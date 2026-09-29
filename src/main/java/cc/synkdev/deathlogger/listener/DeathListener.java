package cc.synkdev.deathlogger.listener;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.manager.ConfigManager;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.manager.FileManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {
    private final DeathLogger core = DeathLogger.getInstance();
    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String message = event.getDeathMessage();
        ItemStack[] inv = p.getInventory().getContents();
        int id = core.deaths.size();
        Death lastDeath = Util.getLastDeath(p);
        Death d = new Death(id, p.getUniqueId(), p.getLocation(), null, message, inv, System.currentTimeMillis());
        FileManager.insert(d);
        if (ConfigManager.isOverrideVanillaMsgs()) {
            event.setDeathMessage(null);
        }
        if (ConfigManager.isAnnouceDeaths()) {
            if (ConfigManager.isSelfOnly()) {
                announceDeath(p, event, id);
            } else {
                Bukkit.getOnlinePlayers().forEach(pl -> announceDeath(pl, event, id));
            }
        }
        if (ConfigManager.isLastDeathTime()) {
            if (ConfigManager.isSelfOnly()) {
                sendLastDeath(p, lastDeath, d.isSelf(p), p);
            } else {
                Bukkit.getOnlinePlayers().forEach(pl -> sendLastDeath(p, lastDeath, d.isSelf(pl), pl));
            }
        }
    }

    private void sendLastDeath(Player p, Death lastDeath, boolean self, Player pl) {
        if (self ? pl.hasPermission("deathlogger.lastdeath.self") : pl.hasPermission("deathlogger.lastdeath.all")) {
            String timeSinceLastDeath = lastDeath == null ? Util.translate("noLastDeath", p.getName()) : Util.translate("lastDeath", p.getName(), Util.formatDuration(System.currentTimeMillis() - lastDeath.getUnix()));
            TextComponent comp = new TextComponent(core.prefix() + timeSinceLastDeath);
            pl.spigot().sendMessage(comp);
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        Death lastDeath = Util.getLastDeath(p);
        if (lastDeath != null && lastDeath.getRespawnLoc() == null) {
            lastDeath.setRespawnLoc(event.getRespawnLocation());
            FileManager.update(lastDeath);
        }
    }

    private void announceDeath(Player p, PlayerDeathEvent event, int id) {
        TextComponent base = new TextComponent(core.prefix()+Util.translate("announceDeath", event.getEntity().getName()));
        boolean self = p.getUniqueId().equals(event.getEntity().getUniqueId());
        if (p.hasPermission("deathlogger.coordinates."+(self ? "self" : "all"))) {
            Location loc = event.getEntity().getLocation();
            base.addExtra(Util.translate("announceDeathWorld", Util.translate("world-"+loc.getWorld().getName())));
        }
        if (p.hasPermission("deathlogger.inventory.view."+(self ? "self" : "all"))) {
            TextComponent invBase = new TextComponent(Util.translate("announceDeathInv"));
            invBase.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/dl inv "+id));
            base.addExtra(invBase);
        }
        p.spigot().sendMessage(base);
        if (p.hasPermission("deathlogger.coordinates."+(self ? "self" : "all"))) {
            Location loc = event.getEntity().getLocation();
            TextComponent comp = new TextComponent(core.prefix()+Util.translate("announceDeathLoc", loc.getBlockX()+"", loc.getBlockY()+"", loc.getBlockZ()+""));
            p.spigot().sendMessage(comp);
        }
    }
}
