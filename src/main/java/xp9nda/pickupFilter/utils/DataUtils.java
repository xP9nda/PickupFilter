package xp9nda.pickupFilter.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import xp9nda.pickupFilter.PickupFilter;

import java.io.*;
import java.util.Base64;

public class DataUtils {

    private final PickupFilter plugin;

    public DataUtils(PickupFilter plugin) {
        this.plugin = plugin;
    }

    public void ensureDirectoryExists(String directoryPath) {
        // Create a file object
        File directory = new File(directoryPath);

        // If the directory does not exist, create it
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    public String itemStackToString(ItemStack itemStack) {
        if (itemStack == null) return null;

        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("item", itemStack.serialize());

            // Convert the config to a Base64-encoded string
            return Base64.getEncoder().encodeToString(config.saveToString().getBytes());

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ItemStack stringToItemStack(String encodedObject) {
        if (encodedObject == null || encodedObject.isEmpty()) return null;

        try {
            // Decode Base64
            String yamlString = new String(Base64.getDecoder().decode(encodedObject));
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(yamlString);

            // Deserialize back into ItemStack
            return ItemStack.deserialize(config.getConfigurationSection("item").getValues(false));

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }



//    // from https://www.youtube.com/watch?v=xIl5v9Iyb08
//    public static String itemStackToString(ItemStack itemStack) {
//        try {
//            //Serialize the item(turn it into byte stream)
//            ByteArrayOutputStream io = new ByteArrayOutputStream();
//            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
//            os.writeObject(itemStack);
//            os.flush();
//
//            byte[] serializedObject = io.toByteArray();
//
//            //Encode the serialized object into to the base64 format
//            return new String(Base64.getEncoder().encode(serializedObject));
//
//        } catch (IOException ex){
//            throw new RuntimeException(ex);
//        }
//    }
//
//    // from https://www.youtube.com/watch?v=xIl5v9Iyb08
//    public static ItemStack stringToItemStack(String encodedObject) {
//        try {
//            //Now we are going to do the reverse: decode the string back into raw bytes
//            //and then deserialize the byte array into an object
//
//            //decode string into raw bytes
//            byte[] serializedObject = Base64.getDecoder().decode(encodedObject);
//
//            //Input stream to read the byte array
//            ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
//            //object input stream to serialize bytes into objects
//            BukkitObjectInputStream is = new BukkitObjectInputStream(in);
//
//            //Use the object input stream to deserialize an object from the raw bytes
//            return (ItemStack) is.readObject();
//
//        } catch (ClassNotFoundException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    public void saveFile(String filePath, String jsonContent) {
        // Try to save the file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonContent);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save file to path: " + filePath);
        }
    }
}
