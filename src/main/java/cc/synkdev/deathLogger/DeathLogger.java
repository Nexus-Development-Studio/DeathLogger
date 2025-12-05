package cc.synkdev.deathLogger;

import cc.synkdev.deathLogger.command.MainCommand;
import cc.synkdev.deathLogger.listener.DeathListener;
import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.deathLogger.manager.FileManager;
import cc.synkdev.synkLibs.bukkit.Analytics;
import cc.synkdev.synkLibs.bukkit.Lang;
import cc.synkdev.synkLibs.components.SynkPlugin;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageKeys;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class DeathLogger extends JavaPlugin implements SynkPlugin {
    @Getter private static DeathLogger instance;
    @Getter private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6DeathLogger&8] » &r");
    public List<Death> deaths = new ArrayList<>();
    public Map<String, String> langMap = new HashMap<>();


    public void onEnable() {
        instance = this;
        Analytics.registerSpl(this);
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        FileManager.create();
        FileManager.read();
        langMap.clear();
        langMap.putAll(Lang.init(this, new File(getDataFolder(), "lang.json")));

        BukkitCommandManager bcm = new BukkitCommandManager(this);

        bcm.getLocales().addMessage(bcm.getLocales().getDefaultLocale(), MessageKeys.PERMISSION_DENIED, Lang.translate("noPermission", this));
        bcm.registerCommand(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        int plId = 22687;
        new Metrics(this, plId);
    }


    public void onDisable() {}

    @Override
    public String name() {
        return "DeathLogger";
    }

    @Override
    public String ver() {
        return "3.0";
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