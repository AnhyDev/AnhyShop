package ink.anh.shop.trading;

import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;

import ink.anh.lingo.AnhyLingo;
import ink.anh.lingo.api.Translator;
import ink.anh.lingo.lang.TranslateItemStack;
import ink.anh.lingo.utils.LangUtils;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.utils.ServerUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;

public class VirtualVillager {
	
    public static boolean openTrading(Player player, Trader trader) {
        if (trader.getTrades().isEmpty()) {
            return false;
        }

        String[] langs = LangUtils.getPlayerLanguage(player);
        String traderName = Translator.translateKyeWorld(trader.getName(), langs, AnhyShop.getInstance().getLanguageManager());
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
        String version = ServerUtils.getServerVersion();
        Class<?> craftItemStackClass;
        Method asCraftCopyMethod;
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
        if (checkItem(item)) {
        	TranslateItemStack translater = new TranslateItemStack(AnhyLingo.getInstance());
        	translater.modifyItem(langs, item);
        }
    }
    
    private static boolean checkItem(ItemStack item) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta());
    }
}
