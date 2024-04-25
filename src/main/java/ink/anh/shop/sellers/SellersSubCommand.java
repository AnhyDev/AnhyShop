package ink.anh.shop.sellers;

import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.messages.MessageChat;
import ink.anh.api.messages.MessageComponents;
import ink.anh.api.messages.MessageContext;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Messenger;
import ink.anh.api.messages.Sender;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.sellers.obj.AbstractSeller;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.VirtualVillager;

public class SellersSubCommand extends Sender {

	AnhyShop shopPlugin;

    public SellersSubCommand(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
    }

    public boolean onSubCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sendMessage(new MessageForFormatting("shop_err_command_format %s ", new String[]{"/shop seller <args ...>"}), MessageType.WARNING, sender);
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "add":
                new TargetResolver(shopPlugin).addSaler(sender, args);
                break;
            case "remove":
                removeSeller(sender, args);
                break;
            case "replace":
                replaceSeller(sender, args);
                break;
            case "open":
                openSellerShop(sender, args);
                break;
            case "view":
                viewSellerDetails(sender, args);
                break;
            case "list":
                listAllSellers(sender, args);
                break;
            default:
                sendMessage(new MessageForFormatting("shop_err_command_args", new String[]{"/shop " + String.join(" ", args)}), MessageType.WARNING, sender);
                break;
        }
        return true;
    }

    private void removeSeller(CommandSender sender, String[] args) {
        int sellerKey = getSellerKey(sender, args);
        if (sellerKey == 0) return; // Вже виведено повідомлення про помилку в getSellerKey

        int result = shopPlugin.getGlobalManager().getSellersManager().removeSeller(sellerKey);
        if (result == 1) {
            sendMessage(new MessageForFormatting("shop_success_remove", new String[]{args[2]}), MessageType.NORMAL, sender);
        } else if (result ==  0) {
            sendMessage(new MessageForFormatting("shop_err_seller_not_found", new String[]{args[2]}), MessageType.WARNING, sender);
        } else if (result ==  2) {
            sendMessage(new MessageForFormatting("shop_err_save_data_base", new String[]{args[2]}), MessageType.WARNING, sender);
        }
    }

    private void replaceSeller(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sendMessage(new MessageForFormatting("shop_err_command_format %s ", new String[]{"/shop seller replace <seller_number> <new_trader_key>"}), MessageType.WARNING, sender);
            return;
        }

        int sellerKey = getSellerKey(sender, args);
        if (sellerKey == 0) return; // Вже виведено повідомлення про помилку в getSellerKey

        String newTraderKey = args[3];
        Trader trader = shopPlugin.getGlobalManager().getTraderManager().getTrader(newTraderKey);
    	
    	if (trader == null) {
            sendMessage(new MessageForFormatting("shop_err_no_trader_found_key", new String[] {"traderKey"}), MessageType.WARNING, sender);
    		return;
    	}
    	
        int result = shopPlugin.getGlobalManager().getSellersManager().replaceTrader(sellerKey, trader);
        if (result == 1) {
            sendMessage(new MessageForFormatting("shop_seller_success_replace_trade", new String[]{args[2], newTraderKey}), MessageType.NORMAL, sender);
        } else {
            sendMessage(new MessageForFormatting("shop_err_seller_not_found", new String[]{args[2]}), MessageType.ERROR, sender);
        }
    }

    private void openSellerShop(CommandSender sender, String[] args) {
        int sellerKey = getSellerKey(sender, args);
        if (sellerKey == 0) return; // Вже виведено повідомлення про помилку в getSellerKey

        AbstractSeller saler = shopPlugin.getGlobalManager().getSellersManager().getSeller(sellerKey);
        if (saler != null && sender instanceof Player) {
        	

            Trader trader = shopPlugin.getGlobalManager().getSellersManager().getTrader(saler.hashCode());

            if (trader == null || trader.getTrades() == null || trader.getTrades().isEmpty()) {
                sendMessage(new MessageForFormatting("shop_err_seller_trade_not_found", new String[]{args[2]}), MessageType.ERROR, sender);
                return;
            }
            
            Player player = (Player) sender;
            if (!VirtualVillager.openTrading(player, trader)) {
                sendMessage(new MessageForFormatting("shop_err_not_possible_open_trade", null), MessageType.WARNING, player);
                return;
            }
        } else {
            sendMessage(new MessageForFormatting("shop_err_seller_not_found", new String[]{args[2]}), MessageType.ERROR, sender);
        }
    }

    private void viewSellerDetails(CommandSender sender, String[] args) {
        int sellerKey = getSellerKey(sender, args);
        if (sellerKey == 0) return; // Вже виведено повідомлення про помилку в getSellerKey

        AbstractSeller saler = shopPlugin.getGlobalManager().getSellersManager().getSeller(sellerKey);
        if (saler != null && saler.getTrader() != null) {
        	String copyText = saler.getTrader().getKey();
        	MessageForFormatting message = new MessageForFormatting("shop_info_details", new String[]{saler.getAdditionalDetails()});
        	MessageForFormatting hoverText = new MessageForFormatting("shop_hover_click_to_copy", new String[]{copyText});
        	
            MessageChat.sendMessageWithCopy(libraryManager, sender, message, hoverText, copyText, MessageType.NORMAL, true);
        } else {
            sendMessage(new MessageForFormatting("shop_err_seller_not_found", new String[]{args[2]}), MessageType.ERROR, sender);
        }
    }

    private void listAllSellers(CommandSender sender, String[] args) {
        Map<Integer, AbstractSeller> sellers = shopPlugin.getGlobalManager().getSellersManager().getAllSalers();
        if (!sellers.isEmpty()) {
            // Створення білдера для всіх компонентів
            MessageComponents.MessageBuilder mBuilder = MessageComponents.builder();
            StringBuilder consoleMessage = new StringBuilder();

            sellers.values().forEach(seller -> {
                String additionalDetails = seller.getAdditionalDetails();
                String copyText = seller.getTrader().getKey();
                MessageContext context = new MessageContext(libraryManager, sender, new MessageForFormatting("shop_hover_click_to_copy", new String[]{copyText}), false);


            	// Створення індивідуального компонента для кожного продавця
                MessageComponents sellerComponent = MessageComponents.builder()
                        .content(additionalDetails)
                        .hoverComponent(context.getmBuilder().build())
                        .clickActionCopy(copyText)
                        .appendNewLine() // Додаємо новий рядок після кожного продавця
                        .build();
                
                // Додавання компонента продавця до головного білдера
                mBuilder.append(sellerComponent);

            	// Додаємо інформацію про продавця до StringBuilder
                if (consoleMessage.length() > 0) {
                    consoleMessage.append("\n");  // Додаємо новий рядок перед додаванням наступного продавця
                }
                consoleMessage.append(additionalDetails);
                
            });

            // Відправлення зібраного мульт-компонента або побудованого рядка
            Messenger.sendMessage(libraryManager.getPlugin(), sender, mBuilder.build(), consoleMessage.toString());

        } else {
            sendMessage(new MessageForFormatting("shop_err_no_sellers", null), MessageType.WARNING, sender);
        }
    }

    private int getSellerKey(CommandSender sender, String[] args) {
        int amount = 0;
        
        if (args.length < 3) {
            sendMessage(new MessageForFormatting("shop_err_missing_arguments", new String[] {String.format("/shop seller %s <number>", args[1])}), MessageType.WARNING, sender);
            return amount;
        }

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) { }
        
        if (amount == 0) {
            sendMessage(new MessageForFormatting("shop_err_invalid_number", new String[]{args[2]}), MessageType.ERROR, sender);
        }
        return amount;
    }
}
