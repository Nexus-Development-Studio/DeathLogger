package cc.synkdev.deathlogger;

import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.nexusCore.bukkit.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.security.CodeSigner;
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

    public static Death getDeath(int id) {
        return core.getDeaths().stream().filter(death -> death.getId() == id).findAny().orElse(null);
    }

    public static void sendMessage(Player p, String msg, String... placeholders) {
        p.sendMessage(core.prefix()+translate(msg, placeholders));
    }

    public static Death getLastDeath(Player pl) {
        List<Death> list = getPlayersDeaths().get(pl.getUniqueId());
        if (list == null || list.isEmpty()) return null;
        return list.getLast();
    }

    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;

        if (totalSeconds == 0) {
            return "0s";
        }

        boolean negative = totalSeconds < 0;
        if (negative) {
            totalSeconds = -totalSeconds;
        }

        LinkedHashMap<String, Long> timeUnits = new LinkedHashMap<>();
        timeUnits.put("d", 86400L);
        timeUnits.put("h", 3600L);
        timeUnits.put("m", 60L);
        timeUnits.put("s", 1L);

        StringBuilder sb = new StringBuilder();
        if (negative) {
            sb.append("-");
        }

        for (Map.Entry<String, Long> entry : timeUnits.entrySet()) {
            long unitSeconds = entry.getValue();
            long count = totalSeconds / unitSeconds;
            if (count > 0) {
                sb.append(count).append(entry.getKey()).append(" ");
                totalSeconds %= unitSeconds;
            }
        }

        return sb.toString().trim();
    }

    public static String getName(UUID player) {
        return Bukkit.getOfflinePlayer(player).getName();
    }
}
