package cc.synkdev.deathlogger.manager;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.object.Death;
import cc.synkdev.json.JSONArray;
import cc.synkdev.json.JSONException;
import cc.synkdev.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

                deathObj.put("location", getLocObj(d.getLoc()));
                if (d.getRespawnLoc() != null) deathObj.put("respawnLocation", getLocObj(d.getRespawnLoc()));

                deathObj.put("message", d.getMsg());
                deathObj.put("timestamp", d.getUnix());
                deathObj.put("inventory", serializeInventory(d.getInv()));
                deaths.put(deathObj);
            }
            playerObj.put("deaths", deaths);
            arr.put(playerObj);

        }
        return arr;
    }

    private static JSONObject getLocObj(Location loc) {
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

                    JSONObject locObj = death.getJSONObject("location");
                    Location loc = new Location(Bukkit.getWorld(locObj.getString("world")), locObj.getInt("x"), locObj.getInt("y"), locObj.getInt("z"));
                    Location respawnLoc = null;
                    if (death.has("respawnLocation")) {
                        JSONObject respawnLocObj =  death.getJSONObject("respawnLocation");
                        respawnLoc =  new Location(Bukkit.getWorld(respawnLocObj.getString("world")), respawnLocObj.getInt("x"), respawnLocObj.getInt("y"), respawnLocObj.getInt("z"));
                    }
                    String message = death.getString("message");
                    long unix = death.getLong("timestamp");
                    ItemStack[] inv = deserializeInventory(death.getString("inventory"));
                    core.getDeaths().add(new Death(id, uuid, loc, respawnLoc, message, inv, unix));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JSONException _) {
            // Ignore, file is empty
        }
    }

    private static String serializeInventory(ItemStack[] inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.length);

            for(ItemStack item : inventory) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException var8) {
            throw new RuntimeException(var8);
        }
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
