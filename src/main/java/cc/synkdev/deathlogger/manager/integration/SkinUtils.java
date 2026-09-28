package cc.synkdev.deathlogger.manager.integration;

import cc.synkdev.deathlogger.DeathLogger;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class SkinUtils {

    public static Optional<SkinProperty> getSkin(String name) {
        try {
            return DeathLogger.getInstance().getSkinsRestorerAPI().getSkinStorage()
                    .findOrCreateSkinData(name, SkinVariant.CLASSIC)
                    .map(InputDataResult::getProperty);
        } catch (DataRequestException | MineSkinException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSkinValue(SkinProperty property) {
        if (property == null) return null;
        return property.getValue();
    }

    public static Optional<SkinProperty> getSkin(UUID uuid, String knownName) {
        String name = (knownName != null && !knownName.isEmpty())
                ? knownName
                : Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName()).orElse("");
        if (name.isEmpty()) return Optional.empty();

        PlayerStorage storage = DeathLogger.getInstance().getSkinsRestorerAPI().getPlayerStorage();
        try {
            Optional<SkinProperty> set = storage.getSkinForPlayer(uuid, name);
            if (set.isPresent()) return set;
        } catch (DataRequestException e) {
            DeathLogger.getInstance().getLogger().warning("Skin lookup failed for " + name + ": " + e.getMessage());
        }
        return getSkin(name);
    }
}
