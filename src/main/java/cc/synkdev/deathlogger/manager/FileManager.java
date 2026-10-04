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
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

                deathObj.put("location", getLocObj(d.getLoc()));
                if (d.getRespawnLoc() != null) deathObj.put("respawnLocation", getLocObj(d.getRespawnLoc()));

                deathObj.put("message", d.getMsg());
                deathObj.put("timestamp", d.getUnix());
                deathObj.put("drops", exportInventory(d.getInv()));
                if (d.getHunger() != -1) deathObj.put("hunger", d.getHunger());
                if (d.getSaturation() != -1.0f) deathObj.put("saturation", d.getSaturation());
                if (d.getXp() != -1) deathObj.put("xp", d.getXp());
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

                    JSONObject locObj = death.getJSONObject("location");
                    Location loc = new Location(Bukkit.getWorld(locObj.getString("world")), locObj.getInt("x"), locObj.getInt("y"), locObj.getInt("z"));
                    Location respawnLoc = null;
                    if (death.has("respawnLocation")) {
                        JSONObject respawnLocObj = death.getJSONObject("respawnLocation");
                        respawnLoc = getLocation(respawnLocObj);
                    }
                    String message = death.getString("message");
                    long unix = death.getLong("timestamp");
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
                    core.getDeaths().add(new Death(id, uuid, loc, respawnLoc, message, inv, unix, hunger, saturation, xp));
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
    private static JSONArray serializeInventoryArray(ItemStack[] inventory) {
        JSONArray array = new JSONArray();
        for (ItemStack item : inventory) {
            array.put(Util.serializeItemstack(item));
        }
        return array;
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
