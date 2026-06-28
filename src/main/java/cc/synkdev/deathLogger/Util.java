package cc.synkdev.deathLogger;

import cc.synkdev.deathLogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Util {
    private static final DeathLogger core = DeathLogger.getInstance();
    public static Map<UUID, List<Death>> getPlayersDeaths() {
        Map<UUID, List<Death>> map = new HashMap<>();
        for (Death d : core.getDeaths()) {
            List<Death> list = map.getOrDefault(d.getPlayer(), new ArrayList<>());
            list.add(d);
            map.put(d.getPlayer(), list);
        }
        return map;
    }

    public static void log(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    public static String formatUnixSeconds(long unixSeconds) {
        return Instant.ofEpochSecond(unixSeconds/1000)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static void debug(String s) {
        if (core.getConfig().getBoolean("debug")) log(core.prefix()+ ChatColor.DARK_GRAY+"[DEBUG]"+ChatColor.GOLD+s);
    }

    public static String translate(String key, String... params) {
        return Lang.translate(key, core, params);
    }
}
