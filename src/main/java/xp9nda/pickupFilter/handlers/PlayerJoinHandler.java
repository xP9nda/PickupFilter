package xp9nda.pickupFilter.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.List;
import java.util.UUID;

public class PlayerJoinHandler implements Listener {

    private final PickupFilter plugin;

    public PlayerJoinHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // Listen to player join events
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // load the player's pickup data (this will create a new empty data object if none exists)
        plugin.getDataHolder().loadPlayerData(playerUUID);

        // get the player's data
        PickupUser playerData = plugin.getDataHolder().getPlayerData(playerUUID);

        // serialize all profiles
        for (PickupProfile profile : playerData.getPickupProfiles().values()) {
            List<String> serializedStrings = profile.getPickupFilter();

            // run through each item and add the serialized string to the data holder
            serializedStrings.forEach(serializedString -> {
                // if the serialized string is not already in the cache, then deserialize it and add it
                ItemStack existingItem = plugin.getDataHolder().getItemStackFromSerializedString(serializedString);

                // if the item is not tracked, then deserialize it and add it to the cache
                if (existingItem == null) {
                    existingItem = plugin.getDataUtils().stringToItemStack(serializedString);
                }

                // increment the number of active profiles tracking the item if the item is in the player's active profile
                boolean isItemInActiveProfile = false;
                if (playerData.getActiveProfileUUID() != null) {
                    if (playerData.getActiveProfileUUID().equals(profile.getProfileUUID())) {
                        plugin.getDataHolder().addPlayerActiveProfileItem(playerUUID, existingItem);
                        isItemInActiveProfile = true;
                    }
                }

                // if the item is not in the player's active profile, then just add the serialized string to the data holder
                if (isItemInActiveProfile) {
                    plugin.getDataHolder().addSerializedStringAndItemStack(serializedString, existingItem);
                } else {
                    plugin.getDataHolder().addSerializedStringAndItemStackWithoutActiveTracking(serializedString, existingItem);
                }
            });
        }
    }

    // listen to player quit events
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // save the player's pickup data asynchronously to prevent lag
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataHolder().savePlayerData(playerUUID);

            // before removing player data, we need to remove the information about the player's active profile from the data holder
            UUID activeProfileUUID = plugin.getDataHolder().getPlayerData(playerUUID).getActiveProfileUUID();

            // if the player had an active profile, they contributed to the active profiles count
            if (activeProfileUUID != null) {
                // get the serialized strings in the active profile
                List<String> serializedStrings = plugin.getDataHolder().getPlayerData(playerUUID).getPickupProfile(activeProfileUUID).getPickupFilter();

                // run through each item and remove the serialized string from the data holder
                serializedStrings.forEach(serializedString -> {
                    plugin.getDataHolder().removeSerializedStringToItemStack(serializedString);
                });
            }

            // remove the player's data from the cache
            plugin.getDataHolder().removePlayerData(playerUUID);
            plugin.getDataHolder().clearPlayerActiveProfileItems(playerUUID);
        });
    }

}