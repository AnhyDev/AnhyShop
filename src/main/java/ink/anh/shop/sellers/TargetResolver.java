package ink.anh.shop.sellers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.util.RayTraceResult;

import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.messages.Sender;
import ink.anh.shop.AnhyShop;
import ink.anh.shop.sellers.obj.*;
import ink.anh.shop.trading.Trader;

public class TargetResolver extends Sender {

	AnhyShop shopPlugin;
	private final double MAX_DISTANCE = 5.0;

    public TargetResolver(AnhyShop shopPlugin) {
    	super(shopPlugin.getGlobalManager());
    	this.shopPlugin = shopPlugin;
	}

    public boolean addSeller(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sendMessage(new MessageForFormatting("shop_err_missing_arguments", new String[] {"/shop seller add <trader_key>"}), MessageType.WARNING, sender);
            return true;
        }

        Player player = (Player) sender;
        String traderKey = args[2];
        
		return interimMethod(player, traderKey);
    }
    
    public boolean interimMethod(Player player, String traderKey) {
    	AbstractSeller saler = resolveTarget(player);
    	if (saler == null) {
            sendMessage(new MessageForFormatting("shop_err_seller_noview_type", null), MessageType.WARNING, player);
    		return false;
    	}
    	Trader trader = shopPlugin.getGlobalManager().getTraderManager().getTrader(traderKey);
    	
    	if (trader == null) {
            sendMessage(new MessageForFormatting("shop_err_no_trader_found_key %s ", new String[] {"traderKey"}), MessageType.WARNING, player);
    		return false;
    	}
    	
    	saler.setTrader(trader);
    	
    	SellersManager sellersManager = shopPlugin.getGlobalManager().getSellersManager();
    	int result = sellersManager.addSeller(saler);
    	
		if (result == 1) {
            sendMessage(new MessageForFormatting("shop_seller_is_created", 
            		new String[] {saler.getAdditionalDetails(), translate(player, trader.getName())}), MessageType.NORMAL, player);
			return true;
		} else if (result == 2) {
            sendMessage(new MessageForFormatting("shop_err_save_data_base", new String[] {saler.getAdditionalDetails()}), MessageType.WARNING, player);
			return false;
		}
        sendMessage(new MessageForFormatting("shop_err_save_already_exists", new String[] {saler.getAdditionalDetails()}), MessageType.WARNING, player);
		return false;
    }
    
    private AbstractSeller resolveTarget(Player player) {
        // Спочатку спробуємо знайти сутність
        RayTraceResult rayTraceEntitiesResult = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            MAX_DISTANCE,
            entity -> entity instanceof LivingEntity && entity != player
        );

        if (rayTraceEntitiesResult != null && rayTraceEntitiesResult.getHitEntity() != null) {
            return createSellerFromEntity(rayTraceEntitiesResult.getHitEntity());
        }

        // Якщо сутність не була знайдена, тоді спробуємо знайти блок
        RayTraceResult rayTraceBlocksResult = player.rayTraceBlocks(MAX_DISTANCE);
        if (rayTraceBlocksResult != null && rayTraceBlocksResult.getHitBlock() != null) {
            return createSellerFromBlock(rayTraceBlocksResult.getHitBlock());
        }

        return null;
    }

    private AbstractSeller createSellerFromEntity(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (livingEntity.getCustomName() != null && !livingEntity.getCustomName().isEmpty()) {
                return new EntitySeller(entity.getType(), livingEntity.getCustomName());
            } else if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                return new VillagerSeller(villager.getProfession(), villager.getVillagerLevel());
            } else if (entity instanceof WanderingTrader) {
                return new VillagerSeller(); // Використання конструктора для блукаючого торговця
            }
        }
        return null;
    }

    private AbstractSeller createSellerFromBlock(Block block) {
        Material material = block.getType();
        SellerType type = handleSellerType(material);
        
        if (type == null) return null;
        
        switch (type) {
            case SIGN:
                BlockData blockData = block.getBlockData();
                if (blockData instanceof Sign || blockData instanceof WallSign) {
                    Sign sign = (Sign) block.getState();
                    @SuppressWarnings("deprecation")
					String[] signTexts = sign.getLines();
                    return new SignSeller(signTexts);
                }
                break;
            case BUTTON:
            case LEVER:
            case DOOR:
                Location loc = block.getLocation();
                return new MechanicalSeller(type, loc);
            default:
                break;
        }
        return null;
    }

    private SellerType handleSellerType(Material material) {
        String materialName = material.toString();
        int lastIndex = materialName.lastIndexOf('_');
        String endsWith = (lastIndex != -1) ? materialName.substring(lastIndex + 1) : materialName;
        switch (endsWith.toUpperCase()) {
            case "BUTTON":
                return SellerType.BUTTON;
            case "DOOR":
                return SellerType.DOOR;
            case "LEVER":
                return SellerType.LEVER;
            case "SIGN":
                return SellerType.SIGN;
            default:
                return null;
        }
    }
}
