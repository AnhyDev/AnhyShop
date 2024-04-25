package ink.anh.shop;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ink.anh.shop.command.CommandShop;
import ink.anh.shop.db.SQLite;
import ink.anh.shop.sellers.InteractionListener;

public class AnhyShop extends JavaPlugin {

	private static AnhyShop instance;
	private GlobalManager manager;
	private SQLite sqlite;

    @Override
    public void onLoad() {
    	checkAnhyLibAPI();
    }

    @Override
    public void onEnable() {
    	instance = this;
		sqlite = new SQLite(this);
		sqlite.load();
		manager = GlobalManager.getManager(this);
        this.getCommand("shop").setExecutor(new CommandShop(this));
        getServer().getPluginManager().registerEvents(new InteractionListener(this), this);
    }
    
	public static AnhyShop getInstance() {
		return instance;
	}

	public GlobalManager getGlobalManager() {
		return manager;
	}

	public void checkAnhyLibAPI() {
	    try {
	        Class.forName("ink.anh.api.LibraryManager");
	        Plugin anhyLibAPI = getServer().getPluginManager().getPlugin("AnhyLibAPI");
	        if (anhyLibAPI == null) {
	            getLogger().severe("AnhyLibAPI plugin not found. The AnhyShop plugin will be disabled.");
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
	        String version = anhyLibAPI.getDescription().getVersion();
	        if (compareVersion(version, "1.5.3") < 0) {
	            getLogger().severe("AnhyLibAPI version is too old. Current version: " + version + ". Required version: 1.5.2 or newer. The AnhyShop plugin will be disabled.");
	            getServer().getPluginManager().disablePlugin(this);
	            return;
	        }
	        getLogger().info("AnhyLibAPI version is satisfactory. Current version: " + version + ". AnhyShop plugin is allowed to be enabled");
	    } catch (ClassNotFoundException e) {
	        getLogger().severe("AnhyLibAPI library not found. The AnhyShop plugin will be disabled.");
	        getServer().getPluginManager().disablePlugin(this);
	        return;
	    }
	}

	private int compareVersion(String currentVersion, String requiredVersion) {
	    String[] currParts = currentVersion.split("\\.", 4); // Обмежуємо кількість частин до чотирьох
	    String[] reqParts = requiredVersion.split("\\.", 4); // Обмежуємо кількість частин до чотирьох

	    int maxComparisonDepth = 3; // Максимальна кількість сегментів для порівняння

	    for (int i = 0; i < maxComparisonDepth; i++) {
	        int currPart = i < currParts.length ? parseIntSafely(currParts[i]) : 0;
	        int reqPart = i < reqParts.length ? parseIntSafely(reqParts[i]) : 0;
	        if (currPart != reqPart) {
	            return Integer.compare(currPart, reqPart);
	        }
	    }
	    return 0;
	}

	private int parseIntSafely(String number) {
	    // Відсікаємо всі символи після першого зустріченого дефісу, підкреслення, двокрапки або пробілу
	    number = number.split("[-_\\s:]")[0];
	    try {
	        return Integer.parseInt(number);
	    } catch (NumberFormatException e) {
	        return 0; // Приймаємо будь-яку нечислову частину як 0
	    }
	}
}
