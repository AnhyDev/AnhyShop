package ink.anh.shop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ink.anh.lingo.AnhyLingo;
import ink.anh.lingo.Permissions;
import ink.anh.lingo.api.Translator;
import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.lingo.utils.LangUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.traders.TradesManager;

public class CommandShop implements CommandExecutor {
	
	private AnhyShop shopPlugin;

	public CommandShop(AnhyShop shopPlugin) {
		this.shopPlugin = shopPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {

            switch (args[0].toLowerCase()) {
            case "reload":
                return reload(sender);
            case "add":
                return addTrade(sender, args);
            case "remove":
                return removeTrade(sender, args);
            case "replace":
                return replaceTrade(sender, args);
            case "deltrader":
                return deleteTrader(sender, args);
            case "trade":
                return trade(sender, args);
            default:
                return false;
            }
        }
		return false;
	}
	
	private boolean replaceTrade(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			TradesManager.replaceTradeIngr(sender, args);
			sendMessage(sender, "PVE rating: " + args[1] + " + " + args[2], MessageType.ERROR);
		}
		return true;
	}
	
	private boolean trade(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			TradesManager trade = new TradesManager(shopPlugin);
			trade.onMarketCMD(sender, args);
			return true;
		}
		return false;
	}
	
	private boolean deleteTrader(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			return TradesManager.rmTradesman(sender, args);
		}
		return false;
	}

	private boolean removeTrade(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			return TradesManager.rmTrade(sender, args);
		}
		return false;
	}

	private boolean addTrade(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			return TradesManager.addTrade(sender, args);
		}
		return false;
	}

    private boolean reload(CommandSender sender) {
    	String[] langs = checkPlayerPermissions(sender, Permissions.RELOAD);
	    if (langs != null && langs[0] == null) {
            return true;
	    }
	    
        if (shopPlugin.getConfigurationManager().reload()) {
            sendMessage(sender, Translator.translateKyeWorld("lingo_language_reloaded ", langs, AnhyLingo.getInstance().getLanguageSystemChat()), MessageType.NORMAL);
            return true;
        }
        return false;
    }


	
    private String[] checkPlayerPermissions(CommandSender sender, String permission) {
        // Перевірка, чи команду виконує консоль
        if (sender instanceof ConsoleCommandSender) {
            return null;
        }

        // Ініціалізація масиву з одним елементом null
        String[] langs = new String[] {null};

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Отримуємо мови для гравця
            langs = LangUtils.getPlayerLanguage(player);

            // Перевіряємо наявність дозволу у гравця
            if (!player.hasPermission(permission)) {
                sendMessage(sender, Translator.translateKyeWorld("lingo_err_not_have_permission ", langs, AnhyLingo.getInstance().getLanguageSystemChat()), MessageType.ERROR);
                return langs;
            }
        }

        return langs;
    }

	private void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(shopPlugin, sender, message, type);
    }
}
