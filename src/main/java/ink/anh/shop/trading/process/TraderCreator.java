package ink.anh.shop.trading.process;

import org.bukkit.command.CommandSender;

import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.api.utils.StringUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.utils.OtherUtils;
import ink.anh.shop.utils.RandomStringGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TraderCreator extends Sender {

	AnhyShop shopPlugin;
    private TraderManager traderManager;

    public TraderCreator(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
        this.traderManager = this.shopPlugin.getGlobalManager().getTraderManager();
    }

    public boolean createTrader(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_CREATE);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
        
        if (args.length < 2) {
            sendMessage(new MessageForFormatting("shop_err_enter_name_trader ", null), MessageType.WARNING, sender);
            return true;
        }

        String traderName = StringUtils.colorize(args[1]);
        String key;

        do {
            key = RandomStringGenerator.generateRandomString(8);
        } while (traderManager.getTrader(key) != null);

        Trader newTrader = new Trader(key, traderName, new ArrayList<>());
        traderManager.addOrUpdateTrader(newTrader);
        sendMessage(new MessageForFormatting("shop_new_trader %s shop_created_with_key %s", new String[] {traderName, key}), MessageType.NORMAL, sender);
        return true;
    }

    public boolean deleteTrader(CommandSender sender, String[] args) {
        
        String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_DELETE);
        if (langs != null && langs[0] == null) {
            return true;
        }
        
        if (args.length < 2) {
            sendMessage(new MessageForFormatting("shop_err_enter_trader_key", null), MessageType.WARNING, sender);
            return true;
        }

        String key = args[1];
        if (traderManager.getTrader(key) == null) {
            sendMessage(new MessageForFormatting("shop_err_no_trader_found_key", null), MessageType.WARNING, sender);
            return true;
        }

        List<Integer> removedSellersKeys = traderManager.removeTrader(key);
        if (removedSellersKeys != null && !removedSellersKeys.isEmpty()) {
            String removedKeysString = removedSellersKeys.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            sendMessage(new MessageForFormatting("shop_removed_merchant2_key", new String[] {key, removedKeysString}), MessageType.NORMAL, sender);
        } else {
            sendMessage(new MessageForFormatting("shop_removed_merchant_key %s", new String[] {key}), MessageType.NORMAL, sender);
        }

        return true;
    }
}
