package ink.anh.shop.traders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import ink.anh.lingo.utils.StringUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.db.MyTradeSQLite;

public class TradesManager {

	public AnhyShop shopPlugin;
	
	public TradesManager(AnhyShop shopPlugin) {
		this.shopPlugin = shopPlugin;
	}

	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}
		if (event.getRightClicked() == null) return;
		Entity entity = event.getRightClicked();
		if (entity == null) return;
		if (entity instanceof LivingEntity) {
			if (entity instanceof Player) {
				Player player = event.getPlayer();
				Player rclick = (Player) entity;
				String name = rclick.getName();
				List<Tradesman> tradesmans = getTradesmanDB();
				Tradesman tradesman = null;
				for (int i = 0; i < tradesmans.size(); i++) {
					if (tradesmans.get(i).getName().equalsIgnoreCase(name)) {
						tradesman = tradesmans.get(i);
					}
				}
				if (tradesman == null) return;

				VirtualVillager.openTrading(player, tradesman);
			}
		}
	}
	public boolean onMarketCMD(CommandSender sender, String[] args){
		Player player = (Player) sender;
		if (!player.hasPermission("AnhyShop.trade.cmd")) return false;
		String name = StringUtils.colorize(args[1]);
		List<Tradesman> tradesmans = getTradesmanDB();
		Tradesman tradesman = null;
		for (int i = 0; i < tradesmans.size(); i++) {
			if (tradesmans.get(i).getName().equalsIgnoreCase(name)) {
				tradesman = tradesmans.get(i);
			}
		}
		if (tradesman == null) return false;
		VirtualVillager.openTrading(player, tradesman);

		return true;
	
	}

	public static boolean rmTradesman (CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (!player.hasPermission("AnhyShop.settraders")) return false;
		if (args.length == 1) {
			sender.sendMessage("Укажите имя торговца");
			return true;
		}
		String name = args[1];
		for (int i = 2; args.length > i; i++) {
			name = name + " " + args[i];
		}
		List<Tradesman> tradesmans = getTradesmanDB();
		name = StringUtils.colorize(name);
		Tradesman trman = null;
		for (int i = 0; i < tradesmans.size(); i++) {
			Tradesman t = tradesmans.get(i);
			if (t.getName().equalsIgnoreCase(name)) {
				delTradesmanDB(t);
				sender.sendMessage("Торговец удален.");
				return true;
			}
		}
		if (trman == null) {
			sender.sendMessage("Этого торговца не существует.");
		}
		return true;
	}

	public static boolean rmTrade (CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (!player.hasPermission("AnhyShop.settraders")) return false;
		ItemStack i1 = player.getInventory().getItem(0);
		ItemStack i2 = player.getInventory().getItem(1);
		ItemStack rt = player.getInventory().getItem(2);
		if (args.length == 1) {
			sender.sendMessage("Укажите имя торговца");
			return true;
		}
		if (rt==null||rt.getType()==Material.AIR) {
			sender.sendMessage("Перепроверьте наличие товара в слотах инвентаря.");
			return true;
		}
		String name = args[1];
		for (int i = 2; args.length > i; i++) {
			name = name + " " + args[i];
		}
		name = StringUtils.colorize(name);
		Trade trade = null;
		if (i2==null||i2.getType()==Material.AIR) {
			trade = new Trade(i1, null, rt);
		}else if (i1==null||i1.getType()==Material.AIR) {
			trade = new Trade(i2, null, rt);
		}else {
			trade = new Trade(i1, i2, rt);
		}
		Tradesman trman = null;
		List<Tradesman> tradesmans = getTradesmanDB();
		for (int i = 0; i < tradesmans.size(); i++) {
			Tradesman t = tradesmans.get(i);
			if (t.getName().equalsIgnoreCase(name)) {
				trman = t;
				if (trman.getTrades().contains(trade)) {
					trman.removeTrade(trade);
					sender.sendMessage("Товар удален.");
				}else {
					sender.sendMessage("У этого торговца нет такого товара.");
					return true;
				}
			}
		}
		if (trman == null) {
			sender.sendMessage("Этого торговца не существует.");
			return true;
		}
		if (trman.getTrades().isEmpty()) {
			sender.sendMessage("У этого торговца не осталось товаров, удаляем.");
			delTradesmanDB(trman);
			sender.sendMessage("Сохранено.");
			return true;
		}
		saveTradesmanDB(trman);
		sender.sendMessage("Сохранено.");
		return true;
	}

	public static boolean addTrade (CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (!player.hasPermission("AnhyShop.settraders")) return false;

		ItemStack i1 = player.getInventory().getItem(0);
		ItemStack i2 = player.getInventory().getItem(1);
		ItemStack rt = player.getInventory().getItem(2);
		if (args.length == 1) {
			sender.sendMessage("Укажите имя торговца");
			return true;
		}
		if (i1==null && i2==null || rt==null) {
			sender.sendMessage("Перепроверьте наличие товара в слотах инвентаря 0,1,2");
			return true;
		}
		String name = args[1];
		for (int i = 2; args.length > i; i++) {
			name = name + " " + args[i];
		}
		
		name = StringUtils.colorize(name);
		Trade trade = null;
		if (i2==null) {
			trade = new Trade(i1, null, rt);
		}else if (i1==null) {
			trade = new Trade(i2, null, rt);
		}else {
			trade = new Trade(i1, i2, rt);
		}
		Tradesman trman = null; 
		List<Tradesman> tradesmans = getTradesmanDB();
		
		for (int i = 0; i < tradesmans.size(); i++) {
			Tradesman t = tradesmans.get(i);
			if (t.getName().equalsIgnoreCase(name)) {
				trman = t;
				if (trman.getTrades().contains(trade)) {
					trman.addTrade(trade);
					sender.sendMessage("Торг обновлен.");
				}else {
					trman.addTrade(trade);
					sender.sendMessage("Торг добавлен.");
				}
			}
		}
		if (trman == null) {
			List<Trade> trs = new ArrayList<>();
			trs.add(trade);
			trman = new Tradesman(name, trs);
			sender.sendMessage("Торговец добавлен.");
		}
		saveTradesmanDB(trman);
		sender.sendMessage("Сохранено.");
		return true;
	}

	public static void replaceTradeIngr (CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (!player.hasPermission("AnhyShop.settraders")) return;

		ItemStack i1 = player.getInventory().getItem(0);
		ItemStack i2 = player.getInventory().getItem(1);
		if (args.length == 1) {
			sender.sendMessage("Укажите имя торговца");
			return;
		}
		if (i1==null && i2==null) {
			sender.sendMessage("Перепроверьте наличие товара в слотах инвентаря 0,1,2");
			return;
		}
		String name = args[1];
		for (int i = 2; args.length > i; i++) {
			name = name + " " + args[i];
		}

		name = StringUtils.colorize(name);
		Tradesman trman = null; 
		List<Tradesman> tradesmans = getTradesmanDB();
		
		for (int i = 0; i < tradesmans.size(); i++) {
			Tradesman t = tradesmans.get(i);
			if (t.getName().equalsIgnoreCase(name)) {
				trman = t;
			}
		}
		List<Trade> newtrades = null;
		if (trman == null) {
			sender.sendMessage("Торговец отсутствует.");
			return;
		} else {
			newtrades = new ArrayList<>();
			for (int i = 0; i < trman.getTrades().size(); i++) {
				Trade trade = trman.getTrades().get(i);
				int amount = 1;
				if (trade.getItem1().isSimilar(i1)) {
					amount = trade.getItem1().getAmount();
					ItemStack re = new ItemStack(i2);
					re.setAmount(amount);
					trade.setItem1(re);
				}
				if (trade.getItem2() != null && trade.getItem2().isSimilar(i1)) {
					amount = trade.getItem2().getAmount();
					ItemStack re = new ItemStack(i2);
					re.setAmount(amount);
					trade.setItem2(re);
				}
				if (trade.getRewardItem() != null && trade.getRewardItem().isSimilar(i1)) {
					amount = trade.getRewardItem().getAmount();
					ItemStack re = new ItemStack(i2);
					re.setAmount(amount);
					trade.setRewardItem(re);
				}
				newtrades.add(trade);
			}
		}
		trman.setTrades(newtrades);
		saveTradesmanDB(trman);
		sender.sendMessage("Сохранено.");
	}
	
	public static List<Tradesman> getTradesmanDB() {
		MyTradeSQLite db = new MyTradeSQLite(AnhyShop.getInstance());
		List<Tradesman> list = db.getTradesmansDB();
		return list;
	}
	public static void saveTradesmanDB(Tradesman trm) {
        Bukkit.getScheduler().runTaskAsynchronously(AnhyShop.getInstance(), () -> {
    		MyTradeSQLite db = new MyTradeSQLite(AnhyShop.getInstance());
    		db.setTradesmanDB(trm);
        });
	}
	public static void delTradesmanDB(Tradesman trm) {
		MyTradeSQLite db = new MyTradeSQLite(AnhyShop.getInstance());
		db.delete(trm);
	}
}
