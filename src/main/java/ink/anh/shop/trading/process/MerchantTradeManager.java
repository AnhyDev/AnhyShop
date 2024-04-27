package ink.anh.shop.trading.process;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageComponents;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Messenger;
import ink.anh.api.messages.Sender;
import ink.anh.api.messages.MessageComponents.MessageBuilder;
import ink.anh.api.utils.StringUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.Permissions;
import ink.anh.shop.trading.Trade;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.VirtualVillager;
import ink.anh.shop.utils.OtherUtils;

public class MerchantTradeManager extends Sender {

	AnhyShop shopPlugin;
    private TraderManager traderManager;

    public MerchantTradeManager(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
        this.traderManager = this.shopPlugin.getGlobalManager().getTraderManager();
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
                sendMessage(new MessageForFormatting("shop_err_trade_already_exists ", null), MessageType.WARNING, sender);
                return true;
            }
        }

        Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
        trader.addTrade(newTrade);
        traderManager.addOrUpdateTrader(trader);

        sendMessage(new MessageForFormatting("shop_trade_added_trader %s", new String[] {traderKey}), MessageType.NORMAL, sender);
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
            sendMessage(new MessageForFormatting("shop_no_trades_found_create_new_trade ", null), MessageType.WARNING, sender);
            Trade newTrade = new Trade(tradeItems[0], tradeItems[1], rt);
            trader.addTrade(newTrade);
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(new MessageForFormatting("shop_trade_is_updated_trader %s", new String[] {traderKey}), MessageType.NORMAL, sender);
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
            sendMessage(new MessageForFormatting("shop_err_no_found_reward_item ", null), MessageType.WARNING, sender);
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
            sendMessage(new MessageForFormatting("shop_err_no_rew_trades_found ", null), MessageType.WARNING, sender);
            return true;
        }

        traderManager.addOrUpdateTrader(trader);
        sendMessage(new MessageForFormatting("shop_trade_with_reward %s shop_removed_from_trader %s", new String[] {rt.getType().toString(), traderKey}), MessageType.ESPECIALLY, sender);

        return true;
    }

    public boolean openTradeForPlayer(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_OPEN);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (args.length < 3) {
            sendMessage(new MessageForFormatting("shop_err_key_trader_nickname_player ", null), MessageType.WARNING, sender);
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
            sendMessage(new MessageForFormatting("shop_err_no_player_found_nickname ", null), MessageType.WARNING, sender);
            return true;
        }

        // Відкриття торгів для гравця
        if (VirtualVillager.openTrading(player, trader)) {
            sendMessage(new MessageForFormatting("shop_err_key_trader_nickname_player %s", new String[] {playerName}), MessageType.WARNING, sender);
            return true;
        }

        sendMessage(new MessageForFormatting("shop_err_not_possible_open_trade %s", new String[] {trader.getKey()}), MessageType.WARNING, sender);
        return true;
    }

    public boolean openTrade(CommandSender sender, String[] args) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_TRADE);
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

        // Відкриття торгів для гравця
        if (VirtualVillager.openTrading(player, trader)) {
            return true;
        }

        sendMessage(new MessageForFormatting("shop_err_not_possible_open_trade %s", new String[] {trader.getKey()}), MessageType.WARNING, sender);
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
        String newName = StringUtils.colorize(args[2]);
        Trader trader = getTrader(sender, traderKey);
        if (trader == null) {
        	return true;
        }

        trader.setName(newName);
        traderManager.addOrUpdateTrader(trader);
        sendMessage(new MessageForFormatting("shop_name_trader_with_key %s shop_name_trader_changed_to %s", new String[] {traderKey, newName}), MessageType.NORMAL, sender);
        return true;
    }

    public boolean listTraders(CommandSender sender) {
    	
    	String[] langs = OtherUtils.checkPlayerPermissions(sender, Permissions.TRADE_VIEW);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
        
        List<Trader> traders = traderManager.getAllTraders();
        if (traders.isEmpty()) {
            sendMessage(new MessageForFormatting("shop_err_no_traders_found ", null), MessageType.WARNING, sender);
            return true;
        }

        MessageBuilder mBuilder = MessageComponents.builder();
        StringBuilder consoleMessage = new StringBuilder();
        
        for (Trader trader : traders) {
        	String traderKey = trader.getKey();
        	
            String hoverMessage = StringUtils.formatString(Translator.translateKyeWorld(libraryManager, "shop_hover_click_to_copy", langs), new String[] {traderKey});

            String traderName = Translator.translateKyeWorld(libraryManager, trader.getName(), langs);
            String message = StringUtils.formatString(Translator.translateKyeWorld(libraryManager, "shop_trader_list_info", langs),
            		new String[]{traderKey, traderName, String.valueOf(trader.getTrades().size())});
            
            MessageComponents traderComponent = MessageComponents.builder()
            		.content(message)
                    .hexColor(MessageType.NORMAL.getColor(true))
                    .hoverMessage(hoverMessage)
                    .clickActionCopy(traderKey)
                    .appendNewLine()
                    .build();
            mBuilder.append(traderComponent);

            if (consoleMessage.length() > 0) {
                consoleMessage.append("\n");
            }
            consoleMessage.append(message);
        }
        Messenger.sendMessage(shopPlugin, sender, mBuilder.build(), consoleMessage.toString());
        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendMessage(new MessageForFormatting("shop_err_command_only_player ", null), MessageType.WARNING, sender);
            return false;
        }
        return true;
    }

    private Trader getTrader(CommandSender sender, String traderKey) {
        Trader trader = traderManager.getTrader(traderKey);
        if (trader == null) {
            sendMessage(new MessageForFormatting("shop_err_no_trader_found_key ", null), MessageType.WARNING, sender);
        }
        return trader;
    }

    private ItemStack[] getTradeItems(Player player) {
        ItemStack firstItem = player.getInventory().getItem(0);
        ItemStack secondItem = player.getInventory().getItem(1);

        // Перевірка, чи перший предмет дорівнює null або повітрю
        if ((firstItem == null || firstItem.getType().isAir()) &&
            secondItem != null && !secondItem.getType().isAir()) {
            // Обмін місцями, якщо другий предмет не дорівнює null або повітрю
            firstItem = secondItem;
            secondItem = player.getInventory().getItem(0); // Оновлення другого предмета
        }

        // Повернення масиву предметів
        return new ItemStack[] {
            firstItem,
            secondItem,
            player.getInventory().getItem(2)
        };
    }

    private boolean validateTradeItems(CommandSender sender, ItemStack[] tradeItems) {
        ItemStack i1 = tradeItems[0];
        ItemStack i2 = tradeItems[1];
        ItemStack rt = tradeItems[2];

        if ((i1 == null || i1.getType().isAir()) && (i2 == null || i2.getType().isAir()) || rt == null || rt.getType().isAir()) {
            sendMessage(new MessageForFormatting("shop_err_no_required_items_slots ", null), MessageType.WARNING, sender);
            return false;
        }
        return true;
    }
}
