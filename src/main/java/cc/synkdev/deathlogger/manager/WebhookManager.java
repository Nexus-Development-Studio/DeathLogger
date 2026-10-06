package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.DiscordWebhook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WebhookManager {
    private WebhookManager() {
        /* This utility class should not be instantiated */
    }

    private static final boolean hasPAPI = Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer") && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    public static void sendDeathWebhook(Death death) {
        if (ConfigManager.getWebhookUrl() == null || ConfigManager.getWebhookUrl().isEmpty()) return;

        DiscordWebhook wh = new DiscordWebhook(ConfigManager.getWebhookUrl());
        wh.setUsername("DeathLogger");
        wh.setAvatarUrl("https://cdn.modrinth.com/data/cYG1qwqe/d79d05265d12f7a38f9feaaead09aae14ee14127.png");

        String formatted = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(death.getUnix()));

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setTitle(Util.translate("webhookTitle", death.getPlayerName()))
                .setThumbnail(hasPAPI ? PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(death.getPlayer()), "https://mc-heads.net/head/%skinsrestorer_texture_id_or_steve%.png") : "https://mc-heads.net/head/"+death.getPlayerName())
                .setDescription(death.getMsg())
                .setColor(Color.RED);
        embed.addField(Util.translate("deathTime").substring(2), formatted, true);
        if (ConfigManager.isWebhookDeathLocation()) {
            embed.addField(Util.translate("deathLoc").substring(2), Util.getLocString(death.getLoc()), true);
        }
        if (ConfigManager.isWebhookXp()) {
            embed.addField(Util.translate("deathXP").substring(2), String.valueOf(death.getXp()), true);
        }
        if (ConfigManager.isWebhookHunger()) {
            embed.addField(Util.translate("deathHunger").substring(2), String.valueOf(death.getHunger()), true);
        }
        if (ConfigManager.isWebhookTimeSinceLastDeath()) {
            Death lastDeath = null;
            if (Util.getPlayersDeaths().get(death.getPlayer()).size() > 1) {
                lastDeath = Util.getPlayersDeaths().get(death.getPlayer()).get(Util.getPlayersDeaths().get(death.getPlayer()).size() - 2);
            }
            String timeSinceLastDeath = lastDeath == null ? Util.translate("noLastDeath", death.getPlayerName()).substring(2) : Util.translate("lastDeath", death.getPlayerName(), Util.formatDuration(System.currentTimeMillis() - lastDeath.getUnix())).substring(2);
            embed.addField(Util.translate("deathTimeSinceLast").substring(2), timeSinceLastDeath, true);
        }
        wh.addEmbed(embed);
        try {
            wh.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
