package ink.anh.shop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.sellers.SellersSubCommand;
import ink.anh.shop.trading.process.MerchantTradeManager;
import ink.anh.shop.trading.process.TraderCreator;
import ink.anh.shop.utils.OtherUtils;

public class CommandShop extends Sender implements CommandExecutor {

	AnhyShop shopPlugin;
	
	public CommandShop(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {

            switch (args[0].toLowerCase()) {
            case "newtrader":
            case "newt":
            case "new":
            	return new TraderCreator(shopPlugin).createTrader(sender, args);
            case "deltrader":
            case "delete":
            case "delt":
            case "del":
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
            case "open":
            	return new MerchantTradeManager(shopPlugin).openTradeForPlayer(sender, args);
            case "trade":
            	return new MerchantTradeManager(shopPlugin).openTrade(sender, args);
            case "seller":
            	return new SellersSubCommand(shopPlugin).onSubCommand(sender, args);
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
	    
        if (shopPlugin.getGlobalManager().reload()) {
            sendMessage(new MessageForFormatting("shop_language_reloaded ", new String[] {}), MessageType.NORMAL, sender);
            return true;
        }
        return false;
    }
}
