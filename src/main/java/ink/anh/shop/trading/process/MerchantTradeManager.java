package ink.anh.shop.trading.process;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trade;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.VirtualVillager;

public class MerchantTradeManager {

    private TraderManager traderManager;
    private AnhyShop shopPlugin;

    public MerchantTradeManager(AnhyShop shopPlugin) {
        this.shopPlugin = shopPlugin;
        this.traderManager = this.shopPlugin.getTraderManager();
    }

    private boolean isPlayer(CommandSender sender, String permissions) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "Ця команда може виконуватися тільки гравцем.", MessageType.WARNING);
            return false;
        } else if (!sender.hasPermission(permissions)) {
            sendMessage(sender, "shop_err_not_have_permission ", MessageType.WARNING);
            return false;
        }
        return true;
    }

    private Trader getTrader(CommandSender sender, String traderKey) {
        Trader trader = traderManager.getTrader(traderKey);
        if (trader == null) {
            sendMessage(sender, "Торговець з таким ключем не знайдений.", MessageType.WARNING);
        }
        return trader;
    }

    private ItemStack[] getTradeItems(Player player) {
        return new ItemStack[] {
            player.getInventory().getItem(0),
            player.getInventory().getItem(1),
            player.getInventory().getItem(2)
        };
    }

    private boolean validateTradeItems(CommandSender sender, ItemStack[] tradeItems) {
        ItemStack i1 = tradeItems[0];
        ItemStack i2 = tradeItems[1];
        ItemStack rt = tradeItems[2];

        if ((i1 == null || i1.getType().isAir()) && (i2 == null || i2.getType().isAir()) || rt == null || rt.getType().isAir()) {
            sendMessage(sender, "Необхідно мати хоча б один предмет для торгівлі та предмет нагороди в слотах інвентаря.", MessageType.WARNING);
            return false;
        }
        return true;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(shopPlugin, sender, message, type);
    }

    public boolean addTrade(CommandSender sender, String[] args) {
        if (!isPlayer(sender, Permissions.TRADE_ADD) || args.length < 2) {
        	return true;
        }

        Player player = (Player) sender;
        ItemStack[] tradeItems = getTradeItems(player);
        if (!validateTradeItems(sender, tradeItems)) {
        	return true;
        }

        String traderKey = args[1];
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        ItemStack rt = tradeItems[2];
        for (Trade existingTrade : trader.getTrades()) {
            if (existingTrade.getRewardItem().isSimilar(rt)) {
                sendMessage(sender, "У цього торговця вже є торг з такою ж нагородою.", MessageType.WARNING);
                return true;
            }
        }

        Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
        trader.addTrade(newTrade);
        traderManager.addOrUpdateTrader(trader);

        sendMessage(sender, "Торг додано до торговця " + traderKey, MessageType.NORMAL);
        return true;
    }

    public boolean replaceTrade(CommandSender sender, String[] args) {
        if (!isPlayer(sender, Permissions.TRADE_REPLACE) || args.length < 2) {
        	return true;
        }

        Player player = (Player) sender;
        ItemStack[] tradeItems = getTradeItems(player);
        if (!validateTradeItems(sender, tradeItems)) {
        	return true;
        }

        String traderKey = args[1];
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        ItemStack rt = tradeItems[2];
        boolean tradeReplaced = false;
        for (Trade existingTrade : trader.getTrades()) {
            if (existingTrade.getRewardItem().isSimilar(rt)) {
                existingTrade.setItem1(tradeItems[0]);
                existingTrade.setItem2(tradeItems[1]);
                existingTrade.setRewardItem(rt);
                tradeReplaced = true;
                break;
            }
        }

        if (!tradeReplaced) {
            sendMessage(sender, "Торг з такою нагородою не знайдено. Створення нового торгу.", MessageType.WARNING);
            Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
            trader.addTrade(newTrade);
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(sender, "Торг оновлено для торговця " + traderKey, MessageType.NORMAL);
        return true;
    }

    public boolean removeTrade(CommandSender sender, String[] args) {
        if (!isPlayer(sender, Permissions.TRADE_REMOVE) || args.length < 2) {
            return true;
        }

        String traderKey = args[1];
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        Player player = (Player) sender;
        ItemStack rt = player.getInventory().getItem(2);
        if (rt == null || rt.getType().isAir()) {
            sendMessage(sender, "Необхідно мати предмет нагороди в третьому слоті інвентаря.", MessageType.WARNING);
            return true;
        }

        boolean tradeRemoved = false;
        for (Trade existingTrade : new ArrayList<>(trader.getTrades())) {
            if (existingTrade.getRewardItem().isSimilar(rt)) {
                trader.removeTrade(existingTrade);
                tradeRemoved = true;
                return true;
            }
        }

        if (!tradeRemoved) {
            sendMessage(sender, "Торг з такою нагородою не знайдено.", MessageType.WARNING);
            return true;
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(sender, "Торг з нагородою " + rt.getType().toString() + " видалено у торговця " + traderKey, MessageType.ESPECIALLY);
        return true;
    }

    public boolean openTradeForPlayer(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "Необхідно вказати ключ торговця та нікнейм гравця.", MessageType.WARNING);
            return true;
        }

        // Перевірка дозволів виконується тільки для гравців, а не для консолі
        if (sender instanceof Player && !sender.hasPermission(Permissions.TRADE_OPEN)) {
            sendMessage(sender, "shop_err_not_have_permission ", MessageType.WARNING);
            return true;
        }

        String traderKey = args[1];
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        String playerName = args[2];
        Player player = shopPlugin.getServer().getPlayer(playerName);
        if (player == null) {
            sendMessage(sender, "Гравець з таким нікнеймом не знайдений.", MessageType.WARNING);
            return true;
        }

        // Відкриття торгів для гравця
        VirtualVillager.openTrading(player, trader);
        sendMessage(sender, "Торги відкрито для гравця " + playerName, MessageType.NORMAL);
        return true;
    }
    
    public boolean changeTraderName(CommandSender sender, String[] args) {
        if (!isPlayer(sender, Permissions.TRADE_CHANGE_NAME) || args.length < 3) {
        	return true;
        }

        String traderKey = args[1];
        String newName = args[2];
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        trader.setName(newName);
        traderManager.addOrUpdateTrader(trader);
        sendMessage(sender, "Ім'я торговця з ключем " + traderKey + " змінено на " + newName, MessageType.NORMAL);
        return true;
    }

    public boolean listTraders(CommandSender sender) {
    	
        if (sender instanceof Player && !sender.hasPermission(Permissions.TRADE_VIEW)) {
            sendMessage(sender, "shop_err_not_have_permission ", MessageType.WARNING);
            return true;
        }
        
        List<Trader> traders = traderManager.getAllTraders();
        if (traders.isEmpty()) {
            sendMessage(sender, "Торговців не знайдено.", MessageType.WARNING);
            return true;
        }

        for (Trader trader : traders) {
            String message = String.format("Ключ: %s, Ім'я: %s, Кількість торгів: %d",
                                          trader.getKey(), trader.getName(), trader.getTrades().size());
            sendMessage(sender, message, MessageType.NORMAL);
        }
        return true;
    }
}
