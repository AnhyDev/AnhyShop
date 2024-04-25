package ink.anh.shop.sellers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import ink.anh.shop.AnhyShop;
import ink.anh.shop.db.SQLiteSellers;
import ink.anh.shop.sellers.obj.AbstractSeller;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.process.TraderManager;

public class SellersManager {

    private static volatile SellersManager instance;
    private Map<Integer, AbstractSeller> sellerList;
    private SQLiteSellers db;
	private AnhyShop shopPlugin;
    

    private SellersManager() {
        this.sellerList = new ConcurrentHashMap<>();
        this.shopPlugin = AnhyShop.getInstance();
        this.db = new SQLiteSellers(shopPlugin);
    }

    public static SellersManager getInstance() {
        if (instance == null) {
            synchronized (SellersManager.class) {
                if (instance == null) {
                    instance = new SellersManager();
                }
            }
        }
        return instance;
    }

    public int addSaler(AbstractSeller saler) {
    	int key = saler.hashCode();
    	int result = 0;
        if (sellerList.putIfAbsent(key, saler) == null) {
            try {
                db.saveSeller(saler);
                result = 1;
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error adding saler to database", e);
                // Optionally rollback if necessary
                sellerList.remove(key);
                result = 2;
            }
        }
        return result;
    }
    
    public int replaceTrader(int key, Trader trader) {
        int result = 0;  // Ініціалізуємо результат як 0, що позначає, що ключ не був знайдений
        Trader oldTrader = getTrader(key);
        AbstractSeller oldSaler = sellerList.get(key);
        if (oldSaler != null) {
        	oldSaler.setTrader(trader);
            try {
                db.saveSeller(oldSaler); // Спроба зберегти нового продавця в базу даних
                result = 1; // Заміна відбулася успішно і дані оновлені в базі даних
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error updating saler in the database", e);
                oldSaler.setTrader(oldTrader); // Відкат до попереднього продавця, якщо оновлення бази даних не вдалося
                result = 2; // Заміна не вдалася через помилку бази даних
            }
        }
        return result; // Повертаємо результат операції
    }

    public int removeSeller(int key) {
        int result = 0;
        AbstractSeller saler = sellerList.remove(key);
        if (saler != null) {
            try {
                db.deleteSeller(key);
                result = 1;
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error removing saler from database", e);
                // Optionally rollback if necessary
                sellerList.put(key, saler);
                result = 2;
            }
        }
        return result;
    }

    public boolean removeSellers(Trader trader) {
        List<Integer> keysToRemove = sellerList.entrySet()
            .stream()
            .filter(entry -> trader.equals(entry.getValue().getTrader()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (!keysToRemove.isEmpty()) {
            try {
                db.deleteSellers(keysToRemove);
                keysToRemove.forEach(sellerList::remove);
                return true;
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error removing traders from database", e);
                return false;
            }
        }
        return false;
    }

    public AbstractSeller getSeller(int key) {
        return sellerList.get(key);
    }

    public Trader getTrader(int key) {
        return sellerList.get(key).getTrader();
    }

    public Map<Integer, AbstractSeller> getAllSalers() {
        return new ConcurrentHashMap<>(sellerList);
    }

    public void synchronizeSalersAndTraders(TraderManager traderManager) {
        sellerList = new ConcurrentHashMap<>();
        Map<Integer, AbstractSeller> salers = db.getSellers();
        
        // Додаємо або оновлюємо трейдерів
        salers.forEach((key, saler) -> {
            Trader trader = traderManager.getTrader(saler.getTrader().getKey());
            if (trader != null) {
                saler.setTrader(trader);
                sellerList.put(key, saler);
            } else {
                try {
                    db.deleteSeller(key);
                } catch (Exception e) {
                    shopPlugin.getLogger().log(Level.SEVERE, "Error removing saler from database due to missing trader", e);
                }
            }
        });
    }
}

