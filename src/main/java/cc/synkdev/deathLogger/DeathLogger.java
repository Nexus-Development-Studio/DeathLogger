package cc.synkdev.deathLogger;

import cc.synkdev.acf.BukkitCommandManager;
import cc.synkdev.acf.MessageKeys;
import cc.synkdev.bstats.bukkit.Metrics;
import cc.synkdev.bstats.charts.SingleLineChart;
import cc.synkdev.deathLogger.command.DeathsCmd;
import cc.synkdev.deathLogger.command.MainCommand;
import cc.synkdev.deathLogger.listener.DeathListener;
import cc.synkdev.deathLogger.manager.FileManager;
import cc.synkdev.deathLogger.manager.integration.SkinUtils;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import cc.synkdev.nexusCore.bukkit.NexusUtils;
import cc.synkdev.nexusCore.components.NexusPlugin;
import lombok.Getter;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class DeathLogger extends JavaPlugin implements NexusPlugin {
    @Getter private static DeathLogger instance;
    @Getter private SkinsRestorer skinsRestorerAPI;
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6DeathLogger&8] » &r");
    public List<Death> deaths = new ArrayList<>();
    public Map<String, String> langMap = new HashMap<>();
    private File configFile = new File(getDataFolder(), "config.yml");
    public YamlConfiguration config;
    public String lang;
    public SkinUtils skinUtils;


    public void onEnable() {
        instance = this;
        config = NexusUtils.updateConfig(this);
        loadConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        FileManager.create();
        FileManager.read();
        NexusUtils.initLang(this, langMap, lang);

        BukkitCommandManager bcm = new BukkitCommandManager(this);

        bcm.getLocales().addMessage(bcm.getLocales().getDefaultLocale(), MessageKeys.PERMISSION_DENIED, Lang.translate("noPermission", this));
        bcm.registerCommand(new MainCommand());
        bcm.registerCommand(new DeathsCmd());
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Metrics metrics = new Metrics(this, 22687);
        metrics.addCustomChart(new SingleLineChart("death", () -> deaths.size()));

        if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            this.skinsRestorerAPI = SkinsRestorerProvider.get();
            this.skinUtils = new SkinUtils();
        }
    }

    public void loadConfig() {
        reloadConfig();
        lang = getConfig().getString("lang");
    }


    public void onDisable() {}

    @Override
    public String name() {
        return "DeathLogger";
    }

    @Override
    public String ver() {
        return "3.2";
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