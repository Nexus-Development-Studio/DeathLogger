package cc.synkdev.deathlogger.manager.integration;

import cc.synkdev.deathlogger.DeathLogger;
import cc.synkdev.deathlogger.manager.ConfigManager;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.property.SkinVariant;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;

public class SkinUtils {
    private final DeathLogger core = DeathLogger.getInstance();
    public Boolean isActive() {
        return Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer") && ConfigManager.isUseSkinsRestorer();
    }
    public SkinProperty getPlayerSkin(OfflinePlayer player) {
        if (!isActive()) return null;
        PlayerStorage playerStorage = core.getSkinsRestorerAPI().getPlayerStorage();
        Optional<SkinProperty> property;
        try {
            property = playerStorage.getSkinForPlayer(
                    player.getUniqueId(),
                    player.getName()
            );
        } catch (DataRequestException e) {
            throw new RuntimeException(e);
        }
        return property.orElse(null);
    }

    public static Optional<SkinProperty> getSkin(UUID uuid) {
        String name = Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName()).orElse("");
        try {
            Optional<SkinProperty> set = DeathLogger.getInstance().getSkinsRestorerAPI().getPlayerStorage().getSkinForPlayer(uuid, name);
            if (set.isPresent()) {
                return set;
            }
        } catch (DataRequestException e) {
            e.printStackTrace();
        }

        if (!name.isEmpty()) {
            return getSkin(name);
        }
        return Optional.empty();
    }

    public static Optional<SkinProperty> getSkin(String name) {
        try {
            return DeathLogger.getInstance().getSkinsRestorerAPI().getSkinStorage()
                    .findOrCreateSkinData(name, SkinVariant.CLASSIC)
                    .map(InputDataResult::getProperty);
        } catch (DataRequestException | MineSkinException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<SkinProperty> getSkinByOfflinePlayer(OfflinePlayer player) {
        return getSkin(player.getUniqueId());
    }

    public Optional<String> getTextureHash(UUID uuid) {
        return getSkin(uuid).map(PropertyUtils::getSkinTextureHash);
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
