package ink.anh.shop.items;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonSyntaxException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Base64;

public class ItemStackSerializer {

    public static ItemStack deserializeItemStack(String serializedItemStack) {
        try {
            return deserializeBase64(serializedItemStack);
        } catch (IllegalArgumentException e) {
            try {
                return deserialize(serializedItemStack);
            } catch (JsonSyntaxException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }
    
    public static String serializeItemStack(ItemStack itemStack) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("item", itemStack);
        String serialized = yamlConfiguration.saveToString();
        return Base64.getEncoder().encodeToString(serialized.getBytes());
    }

    public static ItemStack deserializeBase64(String base64SerializedItemStack) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        String decoded = new String(Base64.getDecoder().decode(base64SerializedItemStack));
        try {
            yamlConfiguration.loadFromString(decoded);
            return yamlConfiguration.getItemStack("item");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack deserialize(String serializedItemStack) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(serializedItemStack);
            return yamlConfiguration.getItemStack("item");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}

