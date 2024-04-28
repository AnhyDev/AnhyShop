package ink.anh.shop.sellers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.sellers.obj.*;
import ink.anh.shop.trading.Trader;
import ink.anh.shop.trading.VirtualVillager;

public class InteractionListener extends Sender implements Listener {

	AnhyShop shopPlugin;
	
    public InteractionListener(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRightClickEntity(PlayerInteractEntityEvent event) {
    	if (event.isCancelled()) return;
    	
    	Player player = event.getPlayer();
        if (event.getRightClicked() instanceof LivingEntity) {
            SellersManager sellers = shopPlugin.getGlobalManager().getSellersManager();
        	LivingEntity entity = (LivingEntity) event.getRightClicked();
            AbstractSeller seller = null;
        	if (entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
        		String name = entity.getCustomName();
        		EntityType entityType = entity.getType();
            	seller = sellers.getSeller(new EntitySeller(entityType, name).hashCode());
        	} else if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
            	seller = sellers.getSeller(new VillagerSeller(villager.getProfession(), villager.getVillagerLevel()).hashCode());
            } else if (entity instanceof WanderingTrader) {
            	seller = sellers.getSeller(new VillagerSeller().hashCode());
            }
            
            if (seller != null) {
        		event.setCancelled(openTrade(player, seller));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	Block block = event.getClickedBlock();
        	if (block == null) return;

            Material material = block.getType();
            AbstractSeller seller = null;

            SellerType type = handlesellerType(material);
            
            if (type == null) return; 
            
            SellersManager sellers = shopPlugin.getGlobalManager().getSellersManager();
            
            switch (type) {
                case SIGN:
                    BlockData blockData = block.getBlockData();
                    if (blockData instanceof Sign || blockData instanceof WallSign) {
                        // Кастуємо блок до Sign
                        Sign sign = (Sign) block.getState();
                        @SuppressWarnings("deprecation")
						String[] signTexts = sign.getLines();
                        SignSeller tempSeller = new SignSeller(signTexts);
                        int key = tempSeller.hashCode();
                        //Logger.warn(shopPlugin, "SignSeller: " + tempSeller.getSerializeKey() + "\n HashCode: " + tempSeller.hashCode());
                        seller = sellers.getSeller(key);
                    }
                    break;
                case BUTTON:
                case LEVER:
                case DOOR:
                	Location loc = block.getLocation();

                	MechanicalSeller tempSeller = new MechanicalSeller(type, loc);
                    int key = tempSeller.hashCode();
                    //Logger.warn(shopPlugin, "MechanicalSeller: " + tempSeller.getSerializeKey() + "\n HashCode: " + tempSeller.hashCode());
                    
                	seller = sellers.getSeller(key);;
			default:
				break;
            }
            
            if (seller != null && seller.getTrader() != null) {
        		event.setCancelled(openTrade(player, seller));
            }
        }
    }

    private SellerType handlesellerType(Material material) {
    	SellerType type = null;
    	
    	if (material == null) {
    		return type;
    	}
    	
    	String materialName = material.toString();
    	int lastIndex = materialName.lastIndexOf('_');
    	String endsWith = ((lastIndex != -1) ? materialName.substring(lastIndex + 1) : materialName).toUpperCase();
    	
    	switch (endsWith) {
    		case "BUTTON":
    			type = SellerType.BUTTON;
    			break;
    		case "DOOR":
    			type = SellerType.DOOR;
                break;
    		case "LEVER":
    			type = SellerType.LEVER;
                break;
    		case "SIGN":
    			type = SellerType.SIGN;
    	        break;
    	    default:
    	        break;
    	}
		return type;
    }
    
    private boolean openTrade(Player player, AbstractSeller seller) {
        Trader trader = shopPlugin.getGlobalManager().getSellersManager().getTrader(seller.hashCode());

        if (trader == null || trader.getTrades() == null || trader.getTrades().isEmpty()) {
            return false;  // Не скасовуємо стандартну взаємодію, бо торгівля не можлива
        }
        
        if (!VirtualVillager.openTrading(player, trader)) {
            sendMessage(new MessageForFormatting("shop_err_not_possible_open_trade", new String[] {}), MessageType.WARNING, player);
            return false;  // Не скасовуємо стандартну взаємодію, бо торгівля не була відкрита
        }

        return true;  // Скасовуємо стандартну взаємодію, бо торгівля була вдало відкрита
    }
}
