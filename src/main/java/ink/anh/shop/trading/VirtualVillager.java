package ink.anh.shop.trading;

import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.Logger;
import ink.anh.api.utils.LangUtils;
import ink.anh.lingo.AnhyLingo;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.GlobalManager;
import ink.anh.shop.utils.OtherUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

public class VirtualVillager {

	private static GlobalManager manager;
	
	static {
		manager = AnhyShop.getInstance().getGlobalManager();
	}
	
    public static boolean openTrading(Player player, Trader trader) {
        if (trader.getTrades().isEmpty()) {
            return false;
        }

        String[] langs = LangUtils.getPlayerLanguage(player);
        String traderName = Translator.translateKyeWorld(manager, trader.getName(), langs);
        Merchant merchant = Bukkit.createMerchant(traderName);
        setCustomTrades(merchant, translateTrade(langs, trader.getTrades()));
        player.openMerchant(merchant, true);

        return true;
    }

    private static void setCustomTrades(Merchant merchant, List<Trade> trades) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (Trade trade : trades) {
            MerchantRecipe recipe = new MerchantRecipe(trade.getRewardItem(), Integer.MAX_VALUE);
            recipe.addIngredient(trade.getItem1());
            if (trade.hasItem2()) {
                recipe.addIngredient(trade.getItem2());
            }
            recipes.add(recipe);
        }

        merchant.setRecipes(recipes);
    }
    
    private static List<Trade> translateTrade(String[] langs, List<Trade> trades) {
        String version = OtherUtils.getServerVersion();
        Class<?> craftItemStackClass;
        Method asCraftCopyMethod;
<<<<<<< HEAD

        try {
            craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            asCraftCopyMethod = craftItemStackClass.getMethod("asCraftCopy", ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Logger.error(manager.getPlugin(), "\nFailed to load CraftItemStack class or method: " + e.getMessage());
            return trades; // Повертаємо оригінальні торги, якщо переклад неможливий
        }

        List<Trade> translatedTrades = new ArrayList<>();
        for (Trade trade : trades) {
            try {
                ItemStack item1 = translateItem(trade.getItem1(), asCraftCopyMethod, langs);
                ItemStack item2 = trade.hasItem2() ? translateItem(trade.getItem2(), asCraftCopyMethod, langs) : null;
                ItemStack rewardItem = translateItem(trade.getRewardItem(), asCraftCopyMethod, langs);

                Trade translatedTrade = new Trade(trade.getId(), item1, item2, rewardItem);
                translatedTrades.add(translatedTrade);
            } catch (Exception e) {
                Logger.error(manager.getPlugin(), "Failed to translate trade: " + e.getMessage());
            }
=======
        try {
            craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            asCraftCopyMethod = craftItemStackClass.getMethod("asCraftCopy", ItemStack.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        List<Trade> translatedTrades = new ArrayList<>();
        for (Trade trade : trades) {
            ItemStack item1 = translateItem(trade.getItem1(), asCraftCopyMethod, langs);
            ItemStack item2 = translateItem(trade.getItem2(), asCraftCopyMethod, langs);
            ItemStack rewardItem = translateItem(trade.getRewardItem(), asCraftCopyMethod, langs);

            Trade translatedTrade = new Trade(trade.getId(), item1, item2, rewardItem);
            translatedTrades.add(translatedTrade);
>>>>>>> branch 'main' of https://github.com/AnhyDev/AnhyShop.git
        }

        return translatedTrades;
    }

    private static ItemStack translateItem(ItemStack item, Method asCraftCopyMethod, String[] langs) {
        if (item == null) return null;
        try {
            ItemStack craftItem = (ItemStack) asCraftCopyMethod.invoke(null, item);
            translateItemStack(craftItem, langs);
            return craftItem;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void translateItemStack(ItemStack item, String[] langs) {
        if (manager.getAnhyLingo() != null && checkItem(item)) {
        	if (manager.isDebug())
        		Logger.warn(manager.getPlugin(), "Sent for proofreading and translation: " + item.getItemMeta().getDisplayName());
        	
        	ink.anh.lingo.item.TranslateItemStack translator = new ink.anh.lingo.item.TranslateItemStack((AnhyLingo) manager.getAnhyLingo());
        	
        	translator.modifyItem(langs, item, false);
        }
    }
    
    private static boolean checkItem(ItemStack item) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta());
    }
}
