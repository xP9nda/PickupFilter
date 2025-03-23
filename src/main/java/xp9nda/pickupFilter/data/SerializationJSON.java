package xp9nda.pickupFilter.data;

import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;
import xp9nda.pickupFilter.utils.DataUtils;

import java.io.File;
import java.util.UUID;

public class SerializationJSON {

    private final PickupFilter plugin;
    private DataUtils dataUtils;

    public SerializationJSON(PickupFilter plugin) {
        this.plugin = plugin;

        dataUtils = plugin.getDataUtils();
    }

    public void serializePlayerPickupData(PickupUser playerPickupUser) {
        // Generate a file path
        String filePath = plugin.getDataFolder().getAbsolutePath();
        filePath += "/filters/";

        // Ensure the directory exists
        dataUtils.ensureDirectoryExists(filePath);

        filePath += playerPickupUser.getPlayerUUID() + ".json";

        // Serialize the player pickup data
        String jsonContent = plugin.getGson().toJson(playerPickupUser);

        // Save the serialized player pickup data to a file
        dataUtils.saveFile(filePath, jsonContent);
    }

    public PickupUser loadPlayerPickupData(UUID playerUUID) {
        // Generate a file path
        String filePath = plugin.getDataFolder().getAbsolutePath();
        filePath += "/filters/" + playerUUID + ".json";

        PickupUser playerPickupUser = new PickupUser();
        playerPickupUser.setPlayerUUID(playerUUID);

        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            return playerPickupUser;
        }

        try {
            String jsonContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            playerPickupUser = plugin.getGson().fromJson(jsonContent, PickupUser.class);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load player pickup data from file: " + e.getMessage());
        }

        return playerPickupUser;
    }

    public boolean doesPlayerPickupDataExist(UUID uuid) {
        String filePath = plugin.getDataFolder().getAbsolutePath() + "/filters/" + uuid + ".json";
        File dataFile = new File(filePath);
        return dataFile.exists();
    }

    public boolean deletePlayerPickupData(UUID uuid) {
        String filePath = plugin.getDataFolder().getAbsolutePath() + "/filters/" + uuid + ".json";
        File dataFile = new File(filePath);

        try {
            return dataFile.delete();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to delete player pickup data from file: " + e.getMessage());
        }

        return false;
    }
}
