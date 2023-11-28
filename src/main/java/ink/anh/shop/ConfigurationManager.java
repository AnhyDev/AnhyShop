package ink.anh.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import ink.anh.lingo.AnhyLingo;
import ink.anh.lingo.api.Translator;
import ink.anh.lingo.messages.Logger;
import net.md_5.bungee.api.ChatColor;

public class ConfigurationManager {

	private AnhyShop shopPlugin;
    private String pluginName = "AnhyShop";
    private File configFile;
    
    private String defaultLang = "en";

    ConfigurationManager(AnhyShop plugin) {
        this.shopPlugin = plugin;
        this.configFile = new File(shopPlugin.getDataFolder(), "config.yml");
        saveDefaultConfiguration();
        setDataConfig();
    }

    void saveDefaultConfiguration() {
        if (!configFile.exists()) {
            YamlConfiguration defaultConfig = new YamlConfiguration();
            defaultConfig.options().setHeader(logo());
            defaultConfig.set("plugin_name", "AnhyShop");
            try {
                defaultConfig.save(configFile);
                Logger.warn(shopPlugin, "Default configuration created. ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setDataConfig() {
        defaultLang = AnhyLingo.getInstance() != null ? AnhyLingo.getInstance().getConfigurationManager().getDefaultLang() : "en";
        pluginName = ChatColor.translateAlternateColorCodes('&',shopPlugin.getConfig().getString("plugin_name", "AnhyShop"));
    }

	public boolean reload() {
        try {
            this.shopPlugin.reloadConfig();
            setDataConfig();
            shopPlugin.getLanguageManager().reloadLanguages();
            shopPlugin.getTraderManager().reloadTraders();
            Logger.info(shopPlugin, Translator.translateKyeWorld("shop_configuration_reloaded" , new String[] {defaultLang}, shopPlugin.getLanguageManager()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(shopPlugin, Translator.translateKyeWorld("shop_err_reloading_configuration ", new String[] {defaultLang}, shopPlugin.getLanguageManager()));
            return false;
        }
    }

	public String getPluginName() {
		return pluginName;
	}    public static List<String> logo() {
        List<String> asciiArt = new ArrayList<>();

        asciiArt.add("");
        asciiArt.add(" ░█████╗░███╗░░██╗██╗░░██╗██╗░░░██╗░██████╗██╗░░██╗░█████╗░██████╗░");
        asciiArt.add(" ██╔══██╗████╗░██║██║░░██║╚██╗░██╔╝██╔════╝██║░░██║██╔══██╗██╔══██╗");
        asciiArt.add(" ███████║██╔██╗██║███████║░╚████╔╝░╚█████╗░███████║██║░░██║██████╔╝");
        asciiArt.add(" ██╔══██║██║╚████║██╔══██║░░╚██╔╝░░░╚═══██╗██╔══██║██║░░██║██╔═══╝░");
        asciiArt.add(" ██║░░██║██║░╚███║██║░░██║░░░██║░░░██████╔╝██║░░██║╚█████╔╝██║░░░░░");
        asciiArt.add(" ╚═╝░░╚═╝╚═╝░░╚══╝╚═╝░░╚═╝░░░╚═╝░░░╚═════╝░╚═╝░░╚═╝░╚════╝░╚═╝░░░░░");
        asciiArt.add("");

        return asciiArt;
    }

}
