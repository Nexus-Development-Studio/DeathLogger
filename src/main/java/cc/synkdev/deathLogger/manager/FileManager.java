package cc.synkdev.deathLogger.manager;

import cc.synkdev.deathLogger.DeathLogger;
import cc.synkdev.deathLogger.Util;
import cc.synkdev.deathLogger.object.Death;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileManager {
    private static final DeathLogger core = DeathLogger.getInstance();
    private static final File file = new File(core.getDataFolder(), "playerdata.json");
    public static void create() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
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

                JSONObject locObj = new JSONObject();
                locObj.put("world", d.getLoc().getWorld().getName());
                locObj.put("x", d.getLoc().getBlockX());
                locObj.put("y", d.getLoc().getBlockY());
                locObj.put("z", d.getLoc().getBlockZ());
                deathObj.put("location", locObj);

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
    public static void insert(Death d) {
        core.deaths.add(d);

        try {
            Files.writeString(file.toPath(), exportMap().toString(2));
        } catch (IOException e) {
            e.printStackTrace();
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
                    String message = death.getString("message");
                    long unix = death.getLong("timestamp");
                    ItemStack[] inv = deserializeInventory(death.getString("inventory"));
                    core.getDeaths().add(new Death(id, uuid, loc, message, inv, unix));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException ignored){}
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
            var8.printStackTrace();
            return null;
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
}
