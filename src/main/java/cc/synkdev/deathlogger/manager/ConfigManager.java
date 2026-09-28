package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.nexusCore.bukkit.NexusUtils;
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
    }
}
