package ink.anh.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import ink.anh.api.LibraryManager;
import ink.anh.api.lingo.Translator;
import ink.anh.api.lingo.lang.LanguageManager;
import ink.anh.api.messages.Logger;
import ink.anh.shop.lang.LangMessage;
import ink.anh.shop.trading.process.TraderManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.md_5.bungee.api.ChatColor;

public class GlobalManager extends LibraryManager {

    private static GlobalManager instance;
	private AnhyShop shopPlugin;
	private Plugin anhyLingo;
	
	private LanguageManager langManager;
	private TraderManager traderManager;
    private String pluginName;
    private String defaultLang;
    private static BukkitAudiences bukkitAudiences;
    private boolean debug;
	
	private GlobalManager(AnhyShop shopPlugin) {
		super();
		this.shopPlugin = shopPlugin;
		this.saveDefaultConfig();
		this.loadFields(shopPlugin);
	}

    public static synchronized GlobalManager getManager(AnhyShop shopPlugin) {
        if (instance == null) {
            instance = new GlobalManager(shopPlugin);
        }
        return instance;
    }

	public Plugin getAnhyLingo() {
		return anhyLingo;
	}

	private void setAnhyLingo() {
		Plugin lingoPlugin = Bukkit.getServer().getPluginManager().getPlugin("AnhyLingo");
		if (lingoPlugin != null && (lingoPlugin instanceof ink.anh.lingo.AnhyLingo)) {
			this.anhyLingo = (ink.anh.lingo.AnhyLingo) lingoPlugin;
        	Logger.warn(getPlugin(), "AnhyLingo plugin found in the system");
		} else {
        	Logger.error(getPlugin(), "AnhyLingo plugin not found");
		}
	}
    
	@Override
	public Plugin getPlugin() {
		return shopPlugin;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	@Override
	public BukkitAudiences getBukkitAudiences() {
		return bukkitAudiences;
	}

	@Override
	public LanguageManager getLanguageManager() {
		return this.langManager;
	}

	@Override
	public String getDefaultLang() {
		return defaultLang;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public TraderManager getTraderManager() {
		return traderManager;
	}
    
    private void loadFields(AnhyShop shopPlugin) {
    	setAnhyLingo();
        bukkitAudiences = BukkitAudiences.create(shopPlugin);
        defaultLang = shopPlugin.getConfig().getString("language", "en");
        pluginName = ChatColor.translateAlternateColorCodes('&',shopPlugin.getConfig().getString("plugin_name", "AnhyShop"));
        debug = shopPlugin.getConfig().getBoolean("debug", false);
        setLanguageManager();
	    traderManager = TraderManager.getInstance(shopPlugin);
    }

    private void saveDefaultConfig() {
    	File configFile = new File(shopPlugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            YamlConfiguration defaultConfig = new YamlConfiguration();
            defaultConfig.options().setHeader(logo());
            defaultConfig.set("plugin_name", "AnhyShop");
            defaultConfig.set("language", "en");
            //defaultConfig.set("debug", false);
            try {
                defaultConfig.save(configFile);
                Logger.warn(shopPlugin, "Default configuration created. ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLanguageManager() {
        if (this.langManager == null) {
            this.langManager = LangMessage.getInstance(this);;
        } else {
        	this.langManager.reloadLanguages();
        }
    }

	public boolean reload() {
		Bukkit.getScheduler().runTaskAsynchronously(shopPlugin, () -> {
	        try {
	        	saveDefaultConfig();
	            shopPlugin.reloadConfig();
	            loadFields(shopPlugin);
	            traderManager.reloadTraders();
	            Logger.info(shopPlugin, Translator.translateKyeWorld(instance, "shop_configuration_reloaded" , new String[] {defaultLang}));
	        } catch (Exception e) {
	            e.printStackTrace();
	            Logger.error(shopPlugin, Translator.translateKyeWorld(instance, "shop_err_reloading_configuration ", new String[] {defaultLang}));
	        }
		});
        return true;
    }
	
	public static List<String> logo() {
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