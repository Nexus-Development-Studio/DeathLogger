package cc.synkdev.deathLogger;

import cc.synkdev.deathLogger.command.DeathsCmd;
import cc.synkdev.deathLogger.command.MainCommand;
import cc.synkdev.deathLogger.listener.DeathListener;
import cc.synkdev.deathLogger.manager.FileManager;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Analytics;
import cc.synkdev.nexusCore.bukkit.Lang;
import cc.synkdev.nexusCore.components.NexusPlugin;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageKeys;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class DeathLogger extends JavaPlugin implements NexusPlugin {
    @Getter private static DeathLogger instance;
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6DeathLogger&8] » &r");
    public List<Death> deaths = new ArrayList<>();
    public Map<String, String> langMap = new HashMap<>();
    private File configFile = new File(getDataFolder(), "config.yml");
    public YamlConfiguration config;
    public String lang;


    public void onEnable() {
        instance = this;
        updateConfig();
        loadConfig();
        Analytics.registerSpl(this);
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        FileManager.create();
        FileManager.read();
        langMap.clear();
        langMap.putAll(Lang.init(this, new File(getDataFolder(), "lang.json"), lang));

        BukkitCommandManager bcm = new BukkitCommandManager(this);

        bcm.getLocales().addMessage(bcm.getLocales().getDefaultLocale(), MessageKeys.PERMISSION_DENIED, Lang.translate("noPermission", this));
        bcm.registerCommand(new MainCommand());
        bcm.registerCommand(new DeathsCmd());
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Metrics metrics = new Metrics(this, 22687);
        metrics.addCustomChart(new SingleLineChart("death", () -> deaths.size()));
    }

    public void loadConfig() {
        reloadConfig();
        lang = getConfig().getString("lang");
    }

    private void updateConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();
        try {
            if (!configFile.exists()) {
                try {
                    Files.copy(getResource("config.yml"), configFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                File temp = new File(getDataFolder(), "temp-config-"+System.currentTimeMillis()+".yml");
                try {
                    Files.copy(getResource("config.yml"), temp.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(temp);
                FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                boolean changed = false;
                for (String key : tempConfig.getKeys(true)) {
                    if (!config.contains(key)) {
                        config.set(key, tempConfig.get(key));
                        changed = true;
                    }
                }

                if (changed) {
                    config.save(configFile);
                }

                temp.delete();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void onDisable() {}

    @Override
    public String name() {
        return "DeathLogger";
    }

    @Override
    public String ver() {
        return "3.1";
    }

    @Override
    public String dlLink() {
        return "https://modrinth.com/plugin/deathlogger";
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public String lang() {
        return "https://synkdev.cc/storage/translations/lang-pld/DeathLogger/lang-dl.json";
    }

    @Override
    public Map<String, String> langMap() {
        return langMap;
    }
}