package ink.anh.shop;

import org.bukkit.plugin.java.JavaPlugin;

import ink.anh.shop.command.CommandShop;
import ink.anh.shop.db.SQLite;

public class AnhyShop extends JavaPlugin {

	private static AnhyShop instance;
	private GlobalManager manager;
	private SQLite sqlite;

    @Override
    public void onLoad() {
        try {
            Class.forName("ink.anh.api.LibraryManager");
        } catch (ClassNotFoundException e) {
            getLogger().severe("AnhyLibAPI library not found. The AnhyShop plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onEnable() {
    	instance = this;
		sqlite = new SQLite(this);
		sqlite.load();
		manager = GlobalManager.getManager(this);
        this.getCommand("shop").setExecutor(new CommandShop(this));
    }
    
	public static AnhyShop getInstance() {
		return instance;
	}

	public GlobalManager getGlobalManager() {
		return manager;
	}
}
