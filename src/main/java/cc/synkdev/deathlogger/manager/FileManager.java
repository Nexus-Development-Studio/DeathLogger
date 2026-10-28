package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.deathlogger.object.DeathItem;
import cc.synkdev.json.JSONArray;
import cc.synkdev.json.JSONException;
import cc.synkdev.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.*;

public class FileManager {
    private FileManager() {
        /* This utility class should not be instantiated */
    }

    private static final DeathLogger core = DeathLogger.getInstance();
    private static final File file = new File(core.getDataFolder(), "playerdata.json");
    public static void create() {
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new FileSystemException("Failed to create playerdata.json");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static JSONArray exportMap() {
        JSONArray arr = new JSONArray();
        for (Map.Entry<UUID, List<Death>> entry : Util.getPlayersDeaths().entrySet()) {
            JSONObject playerObj = new JSONObject();
            playerObj.put("uuid", entry.getKey().toString());

            JSONArray deaths = new JSONArray();
            for (Death d : entry.getValue()) {
                JSONObject deathObj = new JSONObject();
                deathObj.put("id", d.getId());

                if (d.getLoc() != null) deathObj.put("location", getLocObj(d.getLoc()));
                if (d.getRespawnLoc() != null) deathObj.put("respawnLocation", getLocObj(d.getRespawnLoc()));

                if (d.getMsg() != null) deathObj.put("message", d.getMsg());
                if (d.getUnix() != -1) deathObj.put("timestamp", d.getUnix());
                if (d.getInv() != null) deathObj.put("drops", exportInventory(d.getInv()));
                if (d.getHunger() != -1) deathObj.put("hunger", d.getHunger());
                if (d.getSaturation() != -1.0f) deathObj.put("saturation", d.getSaturation());
                if (d.getXp() != -1) deathObj.put("xp", d.getXp());
                if (d.getPing() != -1) deathObj.put("ping", d.getPing());
                if (d.getTps() != 0.0) deathObj.put("tps", d.getTps());
                deaths.put(deathObj);
            }
            playerObj.put("deaths", deaths);
            arr.put(playerObj);

        }
        return arr;
    }

    private static JSONArray exportInventory(DeathItem[] inv) {
        JSONArray arr = new JSONArray();
        for (DeathItem item : inv) {
            arr.put(item.export());
        }
        return arr;
    }

    public static JSONObject getLocObj(Location loc) {
        JSONObject locObj = new JSONObject();
        locObj.put("world", loc.getWorld().getName());
        locObj.put("x", loc.getBlockX());
        locObj.put("y", loc.getBlockY());
        locObj.put("z", loc.getBlockZ());
        return locObj;
    }

    public static void insert(Death d) {
        core.deaths.add(d);

        try {
            Files.writeString(file.toPath(), exportMap().toString(2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void read() {
        try {
            core.getDeaths().clear();
            String s = Files.readString(file.toPath());
            JSONArray arr = new JSONArray(s);
            for (Object o : arr) {
                JSONObject obj = (JSONObject) o;
                UUID uuid = UUID.fromString(obj.getString("uuid"));
                for (Object d : obj.getJSONArray("deaths")) {
                    JSONObject death = (JSONObject) d;
                    int id = death.getInt("id");

                    Location loc = null;

                    if (death.has("location")) {
                        JSONObject locObj = death.getJSONObject("location");
                        loc = new Location(Bukkit.getWorld(locObj.getString("world")), locObj.getInt("x"), locObj.getInt("y"), locObj.getInt("z"));
                    }
                    Location respawnLoc = null;
                    if (death.has("respawnLocation")) {
                        JSONObject respawnLocObj = death.getJSONObject("respawnLocation");
                        respawnLoc = getLocation(respawnLocObj);
                    }
                    String message = null;
                    if (death.has("message")) message = death.getString("message");
                    long unix = -1L;
                    if (death.has("timestamp")) unix = death.getLong("timestamp");
                    DeathItem[] inv = null;
                    if (death.has("inventory")) inv = ItemTrackerManager.convert(deserializeInventory(death.getString("inventory")));
                    else {
                        if (death.has("drops")) {
                            List<DeathItem> deathInvList = new ArrayList<>();
                            for (Object item : death.getJSONArray("drops")) {
                                JSONObject itemObj = (JSONObject) item;
                                deathInvList.add(new DeathItem(itemObj));
                            }
                            inv = deathInvList.toArray(new DeathItem[0]);
                        }
                    }
                    int hunger = death.has("hunger") ? death.getInt("hunger") : -1;
                    float saturation = death.has("saturation") ? death.getFloat("saturation") : -1.0f;
                    int xp = death.has("xp") ? death.getInt("xp") : -1;
                    int ping = death.has("ping") ? death.getInt("ping") : -1;
                    double tps = death.has("tps") ? death.getDouble("tps") : 0.0;
                    core.getDeaths().add(new Death(id, uuid, loc, respawnLoc, message, inv, unix, hunger, saturation, xp, ping, tps));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JSONException _) {
            // Ignore, file is empty
        }
    }

    public static Location getLocation(JSONObject locObj) {
        return new Location(Bukkit.getWorld(locObj.getString("world")), locObj.getInt("x"), locObj.getInt("y"), locObj.getInt("z"));
    }

    public static ItemStack[] deserializeInventory(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

        int length = dataInput.readInt();
        ItemStack[] items = new ItemStack[length];

        for (int i = 0; i < length; i++) {
            items[i] = (ItemStack) dataInput.readObject();
        }

        dataInput.close();
        return items;
    }

    public static void update(Death death) {
        core.getDeaths().removeIf(d -> d.getId() == death.getId());
        insert(death);
    }
}
