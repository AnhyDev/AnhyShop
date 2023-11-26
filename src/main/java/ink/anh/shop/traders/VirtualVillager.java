package ink.anh.shop.traders;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Merchant;

public class VirtualVillager {
    public static void openTrading(Player player, Tradesman tradesman) {
        Merchant merchant = Bukkit.createMerchant(tradesman.getName());
        setCustomTrades(merchant, tradesman.getTrades());
        player.openMerchant(merchant, true);
    }

    private static void setCustomTrades(Merchant merchant, List<Trade> trades) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (Trade trade : trades) {
            MerchantRecipe recipe = new MerchantRecipe(trade.getRewardItem(), 2048);
            recipe.addIngredient(trade.getItem1());
            if (trade.hasItem2()) {
                recipe.addIngredient(trade.getItem2());
            }
            recipes.add(recipe);
        }

        merchant.setRecipes(recipes);
    }
}
