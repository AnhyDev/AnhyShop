package ink.anh.shop.utils;

import org.bukkit.Bukkit;

public class ServerUtils {

	public static String getServerVersion() {
	    String packageName = Bukkit.getServer().getClass().getPackage().getName();
	    // Версія сервера знаходиться у вигляді 'v1_XX_RY', де XX - версія Minecraft, а Y - ревізія
	    return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

}
