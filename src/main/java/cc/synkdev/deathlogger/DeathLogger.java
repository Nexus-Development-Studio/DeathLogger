package cc.synkdev.deathlogger;

import cc.synkdev.acf.BukkitCommandManager;
import cc.synkdev.acf.MessageKeys;
import cc.synkdev.bstats.bukkit.Metrics;
import cc.synkdev.bstats.charts.SingleLineChart;
import cc.synkdev.deathlogger.command.DeathsCmd;
import cc.synkdev.deathlogger.command.MainCommand;
import cc.synkdev.deathlogger.listener.DeathListener;
import cc.synkdev.deathlogger.listener.PlayerRefundListener;
import cc.synkdev.deathlogger.listener.TrackerListener;
import cc.synkdev.deathlogger.manager.ConfigManager;
import cc.synkdev.deathlogger.manager.FileManager;
import cc.synkdev.deathlogger.manager.integration.SkinUtils;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.faststats.ErrorTracker;
import cc.synkdev.faststats.bukkit.BukkitContext;
import cc.synkdev.faststats.data.Metric;
import cc.synkdev.nexuscore.bukkit.Lang;
import cc.synkdev.nexuscore.bukkit.NexusUtils;
import cc.synkdev.nexuscore.components.NexusPlugin;
import lombok.Getter;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public final List<Death> deaths = new ArrayList<>();
    public final Map<String, String> localLangMap = new HashMap<>();
    private final File configFile = new File(getDataFolder(), "config.yml");
    public SkinUtils skinUtils;

    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    private BukkitContext context;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.init(this);

        FileManager.create();
        FileManager.read();
        NexusUtils.initLang(this, localLangMap, ConfigManager.getLang());

        BukkitCommandManager bcm = new BukkitCommandManager(this);

        bcm.getLocales().addMessage(bcm.getLocales().getDefaultLocale(), MessageKeys.PERMISSION_DENIED, Lang.translate("noPermission", this));
        bcm.registerCommand(new MainCommand());
        bcm.registerCommand(new DeathsCmd());
        //Bukkit.getPluginManager().registerEvents(new TrackerListener(), this);
        //Bukkit.getPluginManager().registerEvents(new PlayerRefundListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Metrics metrics = new Metrics(this, 22687);
        metrics.addCustomChart(new SingleLineChart("death", deaths::size));

        context = new BukkitContext.Factory(this, "f68678b9731013c353d9d16e4184cfbc")
                .errorTrackerService(ERROR_TRACKER)
                .metrics(factory -> factory.addMetric(Metric.number("deaths", deaths::size))
                        .create())
                .create();
        context.ready();

        if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
            this.skinsRestorerAPI = SkinsRestorerProvider.get();
            this.skinUtils = new SkinUtils();
        }
    }

    @Override
    public void onDisable() {
        context.shutdown();
    }

    @Override
    public String name() {
        return "DeathLogger";
    }

    @Override
    public String ver() {
        return "3.5.0";
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
        return localLangMap;
    }
}