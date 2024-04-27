package ink.anh.shop.trading.process;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;

import ink.anh.shop.AnhyShop;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.db.SQLiteTraders;
import ink.anh.shop.sellers.SellersManager;

public class TraderManager {

    private static TraderManager instance;
    private Map<String, Trader> traderMap = new HashMap<>();
    private AnhyShop shopPlugin;

    private TraderManager(AnhyShop shopPlugin) {
        this.shopPlugin = shopPlugin;
        loadTradersFromDatabase();
    }

    public static TraderManager getInstance(AnhyShop shopPlugin) {
        if (instance == null) {
            instance = new TraderManager(shopPlugin);
        }
        return instance;
    }

    // Завантажує трейдерів з бази даних
    private void loadTradersFromDatabase() {
        SQLiteTraders dataBase = new SQLiteTraders(shopPlugin);
        List<Trader> traders = dataBase.getTradesmansDB();
        traderMap.clear(); // Очищення мапи перед завантаженням
        for (Trader trader : traders) {
            traderMap.put(trader.getKey(), trader);
        }
    }

    // Метод для перезавантаження даних
    public void reloadTraders() {
        loadTradersFromDatabase();
    }

    // Додавання або оновлення трейдера
    public void addOrUpdateTrader(Trader trader) {
        traderMap.put(trader.getKey(), trader);
        saveTrader(trader); // Зберігаємо зміни в базу даних
    }

    // Видалення трейдера
    public List<Integer> removeTrader(String key) {
        Trader trader = traderMap.remove(key);
        if (trader != null) {
            delTrader(trader); // Видаляємо з бази даних
            List<Integer> removedSellers = SellersManager.getInstance().removeSellers(trader); // Видаляємо продавців, пов'язаних з трейдером
            return removedSellers;
        }
        return null; // Повертаємо null у випадку, якщо трейдер не знайдений або виникла помилка під час видалення
    }

    public List<Trader> getAllTraders() {
        return new ArrayList<>(traderMap.values());
    }
    
    // Отримання трейдера
    public Trader getTrader(String key) {
        return traderMap.get(key);
    }

    // Зберігання трейдера в базу даних
    private void saveTrader(Trader trader) {
        Bukkit.getScheduler().runTaskAsynchronously(shopPlugin, () -> {
            SQLiteTraders dataBase = new SQLiteTraders(shopPlugin);
            dataBase.setTradesmanDB(trader);
        });
    }

    // Видалення трейдера з бази даних
    private void delTrader(Trader trader) {
        Bukkit.getScheduler().runTaskAsynchronously(shopPlugin, () -> {
            SQLiteTraders dataBase = new SQLiteTraders(shopPlugin);
            dataBase.delete(trader);
        });
    }
}
