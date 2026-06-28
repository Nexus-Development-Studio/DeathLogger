package cc.synkdev.deathLogger.manager.integration;

import cc.synkdev.deathLogger.DeathLogger;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SkinUtils {
    private final DeathLogger core = DeathLogger.getInstance();
    public Boolean isActive() {
        return Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer");
    }
    public SkinProperty getPlayerSkin(Player player) {
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

    public String getSkinValue(SkinProperty property) {
        return property.getValue();
    }
}
