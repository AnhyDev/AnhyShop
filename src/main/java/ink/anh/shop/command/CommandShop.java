package ink.anh.shop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ink.anh.lingo.api.Translator;
import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.process.MerchantTradeManager;
import ink.anh.shop.trading.process.TraderCreator;
import ink.anh.shop.utils.OtherUtils;

public class CommandShop implements CommandExecutor {
	
	private AnhyShop shopPlugin;

	public CommandShop(AnhyShop shopPlugin) {
		this.shopPlugin = shopPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {

            switch (args[0].toLowerCase()) {
            case "newt":
            	return new TraderCreator(shopPlugin).createTrader(sender, args);
            case "delt":
            	return new TraderCreator(shopPlugin).deleteTrader(sender, args);
            case "add":
            	return new MerchantTradeManager(shopPlugin).addTrade(sender, args);
            case "remove":
            	return new MerchantTradeManager(shopPlugin).removeTrade(sender, args);
            case "replace":
                return new MerchantTradeManager(shopPlugin).replaceTrade(sender, args);
            case "rename":
                return new MerchantTradeManager(shopPlugin).changeTraderName(sender, args);
            case "list":
                return new MerchantTradeManager(shopPlugin).listTraders(sender);
            case "trade":
            	return new MerchantTradeManager(shopPlugin).openTradeForPlayer(sender, args);
            case "reload":
                return reload(sender);
            default:
                return false;
            }
        }
		return false;
	}

    private boolean reload(CommandSender sender) {
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.RELOAD);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (shopPlugin.getConfigurationManager().reload()) {
            sendMessage(sender, Translator.translateKyeWorld("shop_language_reloaded ", langs, shopPlugin.getLanguageManager()), MessageType.NORMAL);
            return true;
        }
        return false;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(shopPlugin, sender, message, type);
    }
}
