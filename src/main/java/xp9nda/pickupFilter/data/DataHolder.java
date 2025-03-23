package xp9nda.pickupFilter.data;

import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataHolder {

    private final PickupFilter plugin;

    private HashMap<UUID, PickupUser> allLoadedFilterData = new HashMap<>();

    private HashMap<String, ItemStack> serializedStringsToItemStacks = new HashMap<>();
    private HashMap<ItemStack, String> itemStacksToSerializedStrings = new HashMap<>();

    private HashMap<String, Integer> numberActiveProfilesTrackingSerializedStrings = new HashMap<>();

    private HashMap<UUID, List<ItemStack>> playerActiveProfileItems = new HashMap<>();



    public DataHolder(PickupFilter plugin) {
        this.plugin = plugin;
    }

    public void addPlayerActiveProfileItem(UUID playerUUID, ItemStack itemStack) {
        // add the player uuid to the list if its not there already
        if (!playerActiveProfileItems.containsKey(playerUUID)) {
            playerActiveProfileItems.put(playerUUID, new ArrayList<>());
        }

        // add the item to the list
        playerActiveProfileItems.get(playerUUID).add(itemStack);

        // plugin.getSLF4JLogger().info("Added item to player active profile items: " + playerUUID + " -> " + itemStack.toString());
    }

    public void removePlayerActiveProfileItem(UUID playerUUID, ItemStack itemStack) {
        // check that the uuid and item exist
        if (!playerActiveProfileItems.containsKey(playerUUID) || !playerActiveProfileItems.get(playerUUID).contains(itemStack)) {
            return;
        }

        // remove the item from the list
        playerActiveProfileItems.get(playerUUID).remove(itemStack);

        // plugin.getSLF4JLogger().info("Removed item from player active profile items: " + playerUUID + " -> " + itemStack.toString());
    }

    public boolean isPlayerActiveProfileItem(UUID playerUUID, ItemStack itemStack) {
        if (!playerActiveProfileItems.containsKey(playerUUID)) {
            return false;
        }

        // for each item in the list, check if the item is equal to the item stack
        for (ItemStack item : playerActiveProfileItems.get(playerUUID)) {
            if (item.equals(itemStack)) {
                return true;
            }
        }

        return false;
    }

    public void clearPlayerActiveProfileItems(UUID playerUUID) {
        // Check if the player has any active profile items
        if (!playerActiveProfileItems.containsKey(playerUUID)) {
            return;
        }

        // Get the list of items in the player's active profile
        List<ItemStack> items = playerActiveProfileItems.get(playerUUID);

        // Iterate over each item and update the caches
        for (ItemStack item : items) {
            String serializedString = itemStacksToSerializedStrings.get(item);
            if (serializedString != null) {
                removeSerializedStringToItemStack(serializedString);
            }
        }

        // Clear the player's active profile items
        playerActiveProfileItems.remove(playerUUID);
    }

    public void addSerializedStringAndItemStackWithoutActiveTracking(String serializedString, ItemStack itemStack) {
        serializedStringsToItemStacks.put(serializedString, itemStack);
        itemStacksToSerializedStrings.put(itemStack, serializedString);
    }

    public void addSerializedStringAndItemStack(String serializedString, ItemStack itemStack) {
        // plugin.getSLF4JLogger().info("Added item to serialized string map: " + serializedString + " -> " + itemStack.toString());

        // add 1 to the number of active profiles tracking this serialized string
        Integer numberOfProfilesTrackingThisItem = numberActiveProfilesTrackingSerializedStrings.get(serializedString);

        if (numberOfProfilesTrackingThisItem != null) {
            numberActiveProfilesTrackingSerializedStrings.put(serializedString, numberOfProfilesTrackingThisItem + 1);
            // plugin.getSLF4JLogger().info("Added 1 to the number of profiles tracking this item there are now " + (numberOfProfilesTrackingThisItem + 1) + " profiles tracking this item");
            return;
        }

        serializedStringsToItemStacks.put(serializedString, itemStack);
        itemStacksToSerializedStrings.put(itemStack, serializedString);

        numberActiveProfilesTrackingSerializedStrings.put(serializedString, 1);
        // plugin.getSLF4JLogger().info("Added new item to the number of profiles tracking this item there are now 1 profiles tracking this item");
    }

    public void removeSerializedStringToItemStack(String serializedString) {
        // remove 1 from the number of active profiles tracking this serialized string
        Integer numberOfProfilesTrackingThisItem = numberActiveProfilesTrackingSerializedStrings.get(serializedString);

        // plugin.getSLF4JLogger().info("Removing item from serialized string map: " + serializedString);

        if (numberOfProfilesTrackingThisItem == null) {
            return;
        }

        // if there is only 1 profile tracking this item, remove the item from the maps as no profiles are tracking it
        if (numberOfProfilesTrackingThisItem == 1) {
            numberActiveProfilesTrackingSerializedStrings.remove(serializedString);

            ItemStack itemStack = serializedStringsToItemStacks.get(serializedString);
            serializedStringsToItemStacks.remove(serializedString);
            itemStacksToSerializedStrings.remove(itemStack);

            // plugin.getSLF4JLogger().info("Removed item from the number of profiles tracking this item there are now 0 profiles tracking this item");

            return;
        }

        numberActiveProfilesTrackingSerializedStrings.put(serializedString, numberOfProfilesTrackingThisItem - 1);
        // plugin.getSLF4JLogger().info("Removed 1 from the number of profiles tracking this item there are now " + (numberOfProfilesTrackingThisItem - 1) + " profiles tracking this item");
    }

    public ItemStack getItemStackFromSerializedString(String serializedString) {
        return serializedStringsToItemStacks.get(serializedString);
    }

    public String getSerializedStringFromItemStack(ItemStack itemStack) {
        return itemStacksToSerializedStrings.get(itemStack);
    }


    public void addPlayerData(UUID playerUUID, PickupUser pickupUser) {
        allLoadedFilterData.put(playerUUID, pickupUser);
    }

    public void removePlayerData(UUID playerUUID) {
        allLoadedFilterData.remove(playerUUID);
    }

    public PickupUser getPlayerData(UUID playerUUID) {
        return allLoadedFilterData.get(playerUUID);
    }

    public boolean doesPlayerDataExist(UUID playerUUID) {
        return allLoadedFilterData.containsKey(playerUUID);
    }

    public void savePlayerData(UUID playerUUID) {
        PickupUser playerData = getPlayerData(playerUUID);
        if (playerData == null) {
            return;
        }

        plugin.getSerializationJSON().serializePlayerPickupData(playerData);
    }

    public void loadPlayerData(UUID playerUUID) {
        PickupUser playerData = plugin.getSerializationJSON().loadPlayerPickupData(playerUUID);
        addPlayerData(playerUUID, playerData);
    }

    // save all of the loaded player data
    public void saveAllData() {
        for (UUID playerUUID : allLoadedFilterData.keySet()) {
            savePlayerData(playerUUID);
        }
    }
}
