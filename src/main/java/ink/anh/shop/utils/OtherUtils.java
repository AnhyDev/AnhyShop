package ink.anh.shop.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ink.anh.lingo.api.Translator;
import ink.anh.lingo.messages.MessageType;
import ink.anh.lingo.messages.Messenger;
import ink.anh.lingo.utils.LangUtils;
import ink.anh.shop.AnhyShop;

public class OtherUtils {

	public static String getServerVersion() {
	    String packageName = Bukkit.getServer().getClass().getPackage().getName();
	    // Версія сервера знаходиться у вигляді 'v1_XX_RY', де XX - версія Minecraft, а Y - ревізія
	    return packageName.substring(packageName.lastIndexOf('.') + 1);
	}
	
	public static String[] getLangs(CommandSender sender) {
        String[] langs = null;
        
        if (sender instanceof ConsoleCommandSender) {
            return langs;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Отримуємо мови для гравця
            langs = LangUtils.getPlayerLanguage(player);
        }

        return langs;
    }
	
	public static String[] checkPlayerPermissions(CommandSender sender, String permission) {
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
                sendMessage(sender, Translator.translateKyeWorld("shop_err_not_have_permission ", langs, AnhyShop.getInstance().getLanguageManager()), MessageType.ERROR);
                return langs;
            }
        }

        return langs;
    }

	private static void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(AnhyShop.getInstance(), sender, message, type);
    }

}
