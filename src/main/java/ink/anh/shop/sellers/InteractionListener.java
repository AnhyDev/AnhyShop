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
        	LivingEntity entity = (LivingEntity) event.getRightClicked();
            AbstractSeller saler = null;
        	if (entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
        		String name = entity.getCustomName();
        		EntityType entityType = entity.getType();
        		saler = new EntitySeller(entityType, name);
        	} else if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                saler = new VillagerSeller(villager.getProfession(), villager.getVillagerLevel());
            } else if (entity instanceof WanderingTrader) {
            	saler = new VillagerSeller();
            }
            
            if (saler != null) {
        		event.setCancelled(openTrade(player, saler));
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
            AbstractSeller saler = null;

            SellerType type = handleSalerType(material);
            switch (type) {
                case SIGN:
                    BlockData blockData = block.getBlockData();
                    if (blockData instanceof Sign || blockData instanceof WallSign) {
                        // Кастуємо блок до Sign
                        Sign sign = (Sign) block.getState();
                        @SuppressWarnings("deprecation")
						String[] signTexts = sign.getLines();
                        saler = new SignSeller(signTexts);
                    }
                    break;
                case BUTTON:
                case LEVER:
                case DOOR:
                	Location loc = block.getLocation();
                	saler = new MechanicalSeller(type, loc);
			default:
				break;
            }
            
            if (saler != null) {
        		event.setCancelled(openTrade(player, saler));
            }
        }
    }

    private SellerType handleSalerType(Material material) {
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
    
    private boolean openTrade(Player player, AbstractSeller saler) {
        Trader trader = shopPlugin.getGlobalManager().getSellersManager().getTrader(saler.hashCode());

        if (trader == null || trader.getTrades() == null || trader.getTrades().isEmpty()) {
            return false;  // Не скасовуємо стандартну взаємодію, бо торгівля не можлива
        }
        
        if (!VirtualVillager.openTrading(player, trader)) {
            sendMessage(new MessageForFormatting("shop_err_not_possible_open_trade", null), MessageType.WARNING, player);
            return false;  // Не скасовуємо стандартну взаємодію, бо торгівля не була відкрита
        }

        return true;  // Скасовуємо стандартну взаємодію, бо торгівля була вдало відкрита
    }
}
