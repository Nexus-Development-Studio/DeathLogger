package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.nexuscore.bukkit.NexusUtils;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.FileSystemException;

public class ConfigManager {
    private ConfigManager() {
    }

    @Getter private static YamlConfiguration config;
    @Getter private static String lang;
    @Getter private static boolean useSkinsRestorer;
    @Getter private static boolean annouceDeaths;
    @Getter private static boolean overrideVanillaMsgs;
    @Getter private static boolean lastDeathTime;
    @Getter private static boolean selfOnly;
    @Getter private static String webhookUrl;
    @Getter private static boolean webhookDeathLocation;
    @Getter private static boolean webhookRespawnLocation;
    @Getter private static boolean webhookTimeSinceLastDeath;
    @Getter private static boolean webhookXp;
    @Getter private static boolean webhookHunger;

    public static void init(DeathLogger plugin) {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                try {
                    throw new FileSystemException("Failed to create data folder");
                } catch (FileSystemException e) {
                    throw new RuntimeException(e);
                }
            }

        config = NexusUtils.updateConfig(plugin);
        loadConfig(plugin);

    }

    private static void loadConfig(DeathLogger plugin) {
        plugin.reloadConfig();
        lang = getConfig().getString("lang");
        useSkinsRestorer = getConfig().getBoolean("use-skinsrestorer");
        annouceDeaths = getConfig().getBoolean("announce-deaths.enabled");
        overrideVanillaMsgs = getConfig().getBoolean("announce-deaths.override-vanilla-death-messages");
        lastDeathTime = getConfig().getBoolean("announce-deaths.time-since-last-death");
        selfOnly = getConfig().getBoolean("announce-deaths.self-only");
        webhookUrl = getConfig().getBoolean("webhook.enabled") ? getConfig().getString("webhook.url") : null;
        webhookDeathLocation = getConfig().getBoolean("webhook.values.death-location");
        webhookRespawnLocation = getConfig().getBoolean("webhook.values.respawn-location");
        webhookTimeSinceLastDeath = getConfig().getBoolean("webhook.values.time-since-last-death");
        webhookXp = getConfig().getBoolean("webhook.values.xp");
        webhookHunger = getConfig().getBoolean("webhook.values.hunger");
    }
}
