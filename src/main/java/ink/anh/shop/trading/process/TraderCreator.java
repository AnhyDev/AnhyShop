package ink.anh.shop.trading.process;

import org.bukkit.command.CommandSender;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Messenger;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.GlobalManager;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.utils.OtherUtils;
import ink.anh.shop.utils.RandomStringGenerator;

import java.util.ArrayList;

public class TraderCreator {

    private TraderManager traderManager;
    private AnhyShop shopPlugin;
    private GlobalManager manager;

    public TraderCreator(AnhyShop shopPlugin) {
        this.shopPlugin = shopPlugin;
        this.manager = this.shopPlugin.getGlobalManager();
        this.traderManager = this.shopPlugin.getGlobalManager().getTraderManager();
    }

    public boolean createTrader(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_CREATE);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
        
        if (args.length < 2) {
            sendMessage(sender, translate("shop_err_enter_name_trader ", langs), MessageType.WARNING);
            return true;
        }

        String traderName = args[1];
        String key;

        do {
            key = RandomStringGenerator.generateRandomString(8);
        } while (traderManager.getTrader(key) != null);

        Trader newTrader = new Trader(key, traderName, new ArrayList<>());
        traderManager.addOrUpdateTrader(newTrader);
        sendMessage(sender, translate("shop_new_trader ", langs) + traderName + 
        						translate(" shop_created_with_key ", langs) + key, MessageType.NORMAL);
        return true;
    }

    public boolean deleteTrader(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_DELETE);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
        
        if (args.length < 2) {
            sendMessage(sender, translate("shop_err_enter_trader_key ", langs), MessageType.WARNING);
            return true;
        }

        String key = args[1];
        if (traderManager.getTrader(key) == null) {
            sendMessage(sender, translate("shop_err_no_trader_found_key ", langs), MessageType.WARNING);
            return true;
        }

        traderManager.removeTrader(key);
        sendMessage(sender, translate("shop_removed_merchant_key ", langs) + key, MessageType.NORMAL);
        return true;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(manager, sender, message, type);
    }
	
	private String translate(String key, String[] langs) {
		return Translator.translateKyeWorld(manager, key, langs);
	}
}
