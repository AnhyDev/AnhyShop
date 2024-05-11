package ink.anh.shop;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ink.anh.api.LibraryManager;
import ink.anh.api.lingo.Translator;
import ink.anh.api.lingo.lang.LanguageManager;
import ink.anh.api.messages.Logger;
import ink.anh.shop.lang.LangMessage;
import ink.anh.shop.sellers.SellersManager;
import ink.anh.shop.trading.process.TraderManager;
import net.md_5.bungee.api.ChatColor;

public class GlobalManager extends LibraryManager {

	private static GlobalManager instance;
	private AnhyShop shopPlugin;
	private Plugin anhyLingo;
	
	private LanguageManager langManager;
	private TraderManager traderManager;
	private SellersManager sellersManager;
	private String pluginName;
	private String defaultLang;
	private boolean debug;
	
	private GlobalManager(AnhyShop shopPlugin) {
		super(shopPlugin);
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

	public SellersManager getSellersManager() {
		return sellersManager;
	}
    
    private void loadFields(AnhyShop shopPlugin) {
    	setAnhyLingo();
        defaultLang = shopPlugin.getConfig().getString("language", "en");
        pluginName = ChatColor.translateAlternateColorCodes('&',shopPlugin.getConfig().getString("plugin_name", "AnhyShop"));
        debug = shopPlugin.getConfig().getBoolean("debug", false);
        setLanguageManager();
	    traderManager = TraderManager.getInstance(shopPlugin);
		sellersManager = SellersManager.getInstance();
	    sellersManager.synchronizeSellersAndTraders(traderManager);
    }

    private void saveDefaultConfig() {
        File dataFolder = shopPlugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs(); // Спроба створити папку
            if (!created) {
                Logger.error(shopPlugin, "Could not create plugin directory: " + dataFolder.getPath());
                return;
            }
        }

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            shopPlugin.getConfig().options().copyDefaults(true);
            shopPlugin.saveDefaultConfig();
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
	    	    sellersManager.synchronizeSellersAndTraders(traderManager);
	            Logger.info(shopPlugin, Translator.translateKyeWorld(instance, "shop_configuration_reloaded" , new String[] {defaultLang}));
	        } catch (Exception e) {
	            e.printStackTrace();
	            Logger.error(shopPlugin, Translator.translateKyeWorld(instance, "shop_err_reloading_configuration ", new String[] {defaultLang}));
	        }
		});
        return true;
    }
}
