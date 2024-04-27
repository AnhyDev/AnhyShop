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

    public int addSeller(AbstractSeller seller) {
    	int key = seller.hashCode();
    	int result = 0;
        if (sellerList.putIfAbsent(key, seller) == null) {
            try {
                db.saveSeller(seller);
                result = 1;
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error adding seller to database", e);
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
        AbstractSeller oldSeller = sellerList.get(key);
        if (oldSeller != null) {
        	oldSeller.setTrader(trader);
            try {
                db.saveSeller(oldSeller); // Спроба зберегти нового продавця в базу даних
                result = 1; // Заміна відбулася успішно і дані оновлені в базі даних
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error updating seller in the database", e);
                oldSeller.setTrader(oldTrader); // Відкат до попереднього продавця, якщо оновлення бази даних не вдалося
                result = 2; // Заміна не вдалася через помилку бази даних
            }
        }
        return result; // Повертаємо результат операції
    }

    public int removeSeller(int key) {
        int result = 0;
        AbstractSeller seller = sellerList.remove(key);
        if (seller != null) {
            try {
                db.deleteSeller(key);
                result = 1;
            } catch (Exception e) {
                shopPlugin.getLogger().log(Level.SEVERE, "Error removing seller from database", e);
                // Optionally rollback if necessary
                sellerList.put(key, seller);
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

    public Map<Integer, AbstractSeller> getAllSellers() {
        return new ConcurrentHashMap<>(sellerList);
    }

    public void synchronizeSellersAndTraders(TraderManager traderManager) {
        sellerList = new ConcurrentHashMap<>();
        Map<Integer, AbstractSeller> salers = db.getSellers();

        // Додаємо або оновлюємо трейдерів
        salers.forEach((key, seller) -> {
        	if (seller != null) {
        		String traderKey = seller.getTrader() != null ? seller.getTrader().getKey() : null;
        		Trader trader = traderKey != null ? traderManager.getTrader(traderKey) : null;
        		
                //Logger.warn(shopPlugin, "AbstractSeller: " + seller.hashCode() + ", traderKey = " + traderKey + ", trader != null " + (trader != null));
                
                if (trader != null) {
                    seller.setTrader(trader);
                    sellerList.put(key, seller);
                } else {
                    try {
                        //db.deleteSeller(key);
                    } catch (Exception e) {
                        shopPlugin.getLogger().log(Level.SEVERE, "Error removing seller from database due to missing trader", e);
                    }
                }
        	}
        });
    }
}

