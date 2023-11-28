package ink.anh.shop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ink.anh.lingo.api.lang.LanguageManager;
import ink.anh.shop.command.CommandShop;
import ink.anh.shop.db.SQLite;
import ink.anh.shop.lang.LangMessage;
import ink.anh.shop.trading.process.TraderManager;

public class AnhyShop extends JavaPlugin {

	private static AnhyShop instance;
	private ConfigurationManager configurationManager;
	private LanguageManager languageManager;
	private SQLite sqlite;
	private TraderManager traderManager;

    @Override
    public void onLoad() {
    	instance = this;
		sqlite = new SQLite(this);
		sqlite.load();
    }

    @Override
    public void onEnable() {
    	checkDepends("AnhyLingo");
    	configurationManager = new ConfigurationManager(this);
    	languageManager = LangMessage.getInstance(this);
        this.getCommand("shop").setExecutor(new CommandShop(this));

	    traderManager = TraderManager.getInstance(this);
    }

    @Override
    public void onDisable() {

    }

    private boolean checkDepends(String... depends) {
        boolean missingDepend = false;
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (String depend : depends) {
            if (pluginManager.getPlugin(depend) == null) {
            	this.getLogger().severe("Missing Dependency " + depend);
                missingDepend = true;
            }
        }
        if (missingDepend) {
            pluginManager.disablePlugin(instance);
        }
        return missingDepend;
    }
    
	public static AnhyShop getInstance() {
		return instance;
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	public TraderManager getTraderManager() {
		return traderManager;
	}
}
