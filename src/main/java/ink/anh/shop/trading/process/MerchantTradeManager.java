package ink.anh.shop.trading.process;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ink.anh.lingo.api.Translator;
import ink.anh.lingo.api.lang.LanguageManager;
import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trade;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.VirtualVillager;
import ink.anh.shop.utils.OtherUtils;

public class MerchantTradeManager {

    private TraderManager traderManager;
    private AnhyShop shopPlugin;
    private LanguageManager languageManager;

    public MerchantTradeManager(AnhyShop shopPlugin) {
        this.shopPlugin = shopPlugin;
        this.traderManager = this.shopPlugin.getTraderManager();
        this.languageManager = this.shopPlugin.getLanguageManager();
    }

    public boolean addTrade(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_ADD);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (!isPlayer(sender) || args.length < 2) {
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
                sendMessage(sender, translate("shop_err_trade_already_exists ", langs), MessageType.WARNING);
                return true;
            }
        }

        Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
        trader.addTrade(newTrade);
        traderManager.addOrUpdateTrader(trader);

        sendMessage(sender, translate("shop_trade_added_trader ", langs) + traderKey, MessageType.NORMAL);
        return true;
    }

    public boolean replaceTrade(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_REPLACE);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (!isPlayer(sender) || args.length < 2) {
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
            sendMessage(sender, translate("shop_no_trades_found_create_new_trade ", langs), MessageType.WARNING);
            Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
            trader.addTrade(newTrade);
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(sender, translate("shop_trade_is_updated_trader ", langs) + traderKey, MessageType.NORMAL);
        return true;
    }

    public boolean removeTrade(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_REMOVE);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (!isPlayer(sender) || args.length < 2) {
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
            sendMessage(sender, translate("shop_err_no_found_reward_item ", langs), MessageType.WARNING);
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
            sendMessage(sender, translate("shop_err_no_rew_trades_found ", langs), MessageType.WARNING);
            return true;
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(sender, translate("shop_trade_with_reward ", langs) + rt.getType().toString() + 
        						translate(" shop_removed_from_trader ", langs) + traderKey, MessageType.ESPECIALLY);
        return true;
    }

    public boolean openTradeForPlayer(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_OPEN);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (args.length < 3) {
            sendMessage(sender, translate("shop_err_key_trader_nickname_player ", langs), MessageType.WARNING);
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
            sendMessage(sender, translate("shop_err_no_player_found_nickname ", langs), MessageType.WARNING);
            return true;
        }

        // Відкриття торгів для гравця
        if (VirtualVillager.openTrading(player, trader)) {
            sendMessage(sender, translate("shop_trade_open_player ", langs) + playerName, MessageType.NORMAL);
            return true;
        }

        sendMessage(sender, translate("shop_err_not_possible_open_trade ", langs) + trader.getKey(), MessageType.WARNING);
        return true;
    }
    
    public boolean changeTraderName(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_CHANGE_NAME);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (args.length < 3) {
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
        sendMessage(sender, translate("shop_name_trader_with_key ", langs) + traderKey + 
        						translate(" shop_name_trader_changed_to ", langs) + newName, MessageType.NORMAL);
        return true;
    }

    public boolean listTraders(CommandSender sender) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_VIEW);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
        
        List<Trader> traders = traderManager.getAllTraders();
        if (traders.isEmpty()) {
            sendMessage(sender, translate("shop_err_no_traders_found ", langs), MessageType.WARNING);
            return true;
        }

        for (Trader trader : traders) {
            String message = String.format(translate("shop_trader_key ", langs) + "%s," + 
            								translate(" shop_trader_name ", langs) + "%s," + 
            									translate(" shop_number_trades ", langs) + "%d",
            										trader.getKey(), trader.getName(), trader.getTrades().size());
            sendMessage(sender, message, MessageType.NORMAL);
        }
        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, translate("shop_err_command_only_player ", getLangs(sender)), MessageType.WARNING);
            return false;
        }
        return true;
    }

    private Trader getTrader(CommandSender sender, String traderKey) {
        Trader trader = traderManager.getTrader(traderKey);
        if (trader == null) {
            sendMessage(sender, translate("shop_err_no_trader_found_key ", getLangs(sender)), MessageType.WARNING);
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
            sendMessage(sender, translate("shop_err_no_required_items_slots ", getLangs(sender)), MessageType.WARNING);
            return false;
        }
        return true;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(shopPlugin, sender, message, type);
    }
	
	private String translate(String key, String[] langs) {
		return Translator.translateKyeWorld(key, langs, languageManager);
	}
	
	public String[] getLangs(CommandSender sender) {
		return OtherUtils.getLangs(sender);
	}
}
