package cc.synkdev.deathlogger.object;

import cc.synkdev.deathlogger.manager.PlayerRefundManager;

import java.util.function.Consumer;

public enum TrackerLocation {
    // Holders
    PLAYER(deathItem -> PlayerRefundManager.attemptRefund(deathItem.getPlayerUUID(), deathItem.getItem())),/*
    MOB,
    HORSE,

    // Containers
    CHEST,
    BARREL,
    SHULKER,
    ECHEST,
    CHEST_BOAT,
    CHEST_MINECART,
    HOPPER,
    HOPPER_MINECART,
    DROPPER,
    DISPENSER,
    CRAFTER,
    FURNACE,
    BREWING_STAND,

    // Item storing inside another item
    BUNDLE,

    // Displays and decorative blocks
    IFRAME,
    ARMORSTAND,
    JUKEBOX,
    LECTERN,
    DECORATED_POT,
    CHISELED_BOOKSHELF,
    CAMPFIRE,*/

    DROP(deathItem -> {
        //TODO This
    });

    //DESTROYED;
    public final Consumer<DeathItem> refund;

    TrackerLocation(Consumer<DeathItem> refund) {
        this.refund = refund;
    }
}