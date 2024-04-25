package ink.anh.shop.utils;

import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.messages.MessageChat;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
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
                MessageChat.sendMessage(manager, sender, new MessageForFormatting("shop_err_not_have_permission ", null), MessageType.ERROR, true);
            }
        }

        return langs;
    }

	public static String encryptString(String input, String key) {
	    byte[] inputBytes = input.getBytes();
	    byte[] keyBytes = key.getBytes();
	    byte[] encryptedBytes = new byte[inputBytes.length];

	    for (int i = 0; i < inputBytes.length; i++) {
	        encryptedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
	    }

	    return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	public static String decryptString(String input, String key) {
	    byte[] inputBytes = Base64.getDecoder().decode(input);
	    byte[] keyBytes = key.getBytes();
	    byte[] decryptedBytes = new byte[inputBytes.length];

	    for (int i = 0; i < inputBytes.length; i++) {
	        decryptedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
	    }

	    return new String(decryptedBytes);
	}

	public static String encodeToBase64(String input) {
	    // Кодуємо рядок у Base64
	    return Base64.getEncoder().encodeToString(input.getBytes());
	}
	
	public static String decodeFromBase64(String input) {
	    // Декодуємо рядок з Base64
	    byte[] decodedBytes = Base64.getDecoder().decode(input);
	    return new String(decodedBytes);
	}
	
	public static String encryptAndEncodeBase64(String input, String key) {
	    // Шифруємо вхідний рядок
	    String encrypted = encryptString(input, key);
	    // Кодуємо зашифрований рядок у Base64
	    return encodeToBase64(encrypted);
	}
	
	public static String decodeAndDecryptBase64(String input, String key) {
	    // Декодуємо рядок з Base64
	    String decoded = decodeFromBase64(input);
	    // Дешифруємо декодований рядок
	    return decryptString(decoded, key);
	}
}
