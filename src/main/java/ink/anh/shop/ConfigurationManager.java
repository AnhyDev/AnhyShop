package ink.anh.shop;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import ink.anh.lingo.messages.Logger;
import net.md_5.bungee.api.ChatColor;

public class ConfigurationManager {

	private AnhyShop shopPlugin;
    private String pluginName = "AnhyShop";
    private File configFile;
    
    private String defaultLang = "en";
    private boolean debug;

    ConfigurationManager(AnhyShop plugin) {
        this.shopPlugin = plugin;
        this.configFile = new File(shopPlugin.getDataFolder(), "config.yml");
        saveDefaultConfiguration();
        setDataConfig();
    }

    void saveDefaultConfiguration() {
        if (!configFile.exists()) {
            YamlConfiguration defaultConfig = new YamlConfiguration();
            defaultConfig.set("language", "en");
            defaultConfig.set("plugin_name", "AnhyShop");
            defaultConfig.set("debug", false);
            try {
                defaultConfig.save(configFile);
                Logger.warn(shopPlugin, "Default configuration created. ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setDataConfig() {
        defaultLang = shopPlugin.getConfig().getString("language", "en");
        pluginName = ChatColor.translateAlternateColorCodes('&',shopPlugin.getConfig().getString("plugin_name", "AnhyShop"));
        debug = shopPlugin.getConfig().getBoolean("debug", false);
    }

	public boolean reload() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPluginName() {
		return pluginName;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public boolean isDebug() {
		return debug;
	}
}
