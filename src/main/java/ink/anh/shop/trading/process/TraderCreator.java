package ink.anh.shop.trading.process;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.utils.RandomStringGenerator;

import java.util.ArrayList;

public class TraderCreator {

    private TraderManager traderManager;
    private AnhyShop shopPlugin;

    public TraderCreator(AnhyShop shopPlugin) {
        this.shopPlugin = shopPlugin;
        this.traderManager = this.shopPlugin.getTraderManager();
    }

    public boolean createTrader(CommandSender sender, String[] args) {
    	
        if (sender instanceof Player && !sender.hasPermission(Permissions.TRADE_CREATE)) {
            sendMessage(sender, "shop_err_not_have_permission ", MessageType.WARNING);
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("shop_err_enter_name_trader ");
            return true;
        }

        String traderName = args[1];
        String key;

        do {
            key = RandomStringGenerator.generateRandomString(8);
        } while (traderManager.getTrader(key) != null);

        Trader newTrader = new Trader(key, traderName, new ArrayList<>());
        traderManager.addOrUpdateTrader(newTrader);
        sender.sendMessage("shop_new_trader " + traderName + " shop_created_with_key " + key);
        return true;
    }

    public boolean deleteTrader(CommandSender sender, String[] args) {
    	
        if (sender instanceof Player && !sender.hasPermission(Permissions.TRADE_DELETE)) {
            sendMessage(sender, "shop_err_not_have_permission ", MessageType.WARNING);
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("shop_err_enter_trader_key ");
            return true;
        }

        String key = args[1];
        if (traderManager.getTrader(key) == null) {
            sender.sendMessage("shop_err_no_trader_found_key ");
            return true;
        }

        traderManager.removeTrader(key);
        sender.sendMessage("shop_removed_merchant_key " + key);
        return true;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(shopPlugin, sender, message, type);
    }
}
