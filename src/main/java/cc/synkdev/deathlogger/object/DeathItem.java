package cc.synkdev.deathlogger.object;

import cc.synkdev.deathlogger.Util;
import cc.synkdev.deathlogger.manager.FileManager;
import cc.synkdev.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class DeathItem {
    private UUID uuid;
    private ItemStack item;
    private TrackerLocation locationType;
    private Location location;
    private UUID playerUUID;

    public DeathItem(JSONObject json) {
        this.uuid = json.has("uuid") ? UUID.fromString(json.getString("uuid")) : null;
        this.item = json.getString("item").isEmpty() ? null : Util.deserializeItemstack(json.getString("item"));
        this.locationType = json.has("locationType") ? TrackerLocation.valueOf(json.getString("locationType")) : null;
        this.location = json.has("location") ? FileManager.getLocation(json.getJSONObject("location")) : null;
        this.playerUUID = json.has("playerUUID") ? UUID.fromString(json.getString("playerUUID")) : null;
    }

    public DeathItem(ItemStack item, TrackerLocation locationType, Location location, UUID playerUUID) {
        this.uuid = UUID.randomUUID();
        this.item = item;
        this.locationType = locationType;
        this.location = location;
        this.playerUUID = playerUUID;
    }

    public DeathItem(ItemStack item) {
        this.uuid = null;
        this.item = item;
        this.locationType = null;
        this.location = null;
        this.playerUUID = null;
    }

    public JSONObject export() {
        JSONObject json = new JSONObject();
        if (uuid != null) json.put("uuid", uuid.toString());
        json.put("item", item == null ? "" : Util.serializeItemstack(item));
        if (locationType != null) json.put("locationType", locationType.name());
        if (location != null) json.put("location", FileManager.getLocObj(location));
        if (playerUUID != null) json.put("playerUUID", playerUUID.toString());
        return json;
    }
}
