package ink.anh.shop.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Messenger;
import ink.anh.api.utils.LangUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.GlobalManager;

public class OtherUtils {

	private static GlobalManager manager;
	
	static {
		manager = AnhyShop.getInstance().getGlobalManager();
	}
	
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
                sendMessage(sender, Translator.translateKyeWorld(manager, "shop_err_not_have_permission ", langs), MessageType.ERROR);
                return langs;
            }
        }

        return langs;
    }

	private static void sendMessage(CommandSender sender, String message, MessageType type) {
    	Messenger.sendMessage(manager, sender, message, type);
    }

}
