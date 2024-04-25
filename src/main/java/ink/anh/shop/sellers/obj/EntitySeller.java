package ink.anh.shop.sellers.obj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.EntityType;
import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;

public class EntitySeller extends AbstractSeller {
    private SellerType sellerType;
    private EntityType entity;
    private String customName;

    public EntitySeller(EntityType entity, String customName) {
        this.sellerType = SellerType.ENTITY;
        this.entity = entity;
        this.customName = customName;
    }

    @Override
    public SellerType getSellerType() {
        return sellerType;
    }

    @Override
    public void performAction() {
    	Logger.info(AnhyShop.getInstance(), "Saller " + sellerType.getName() + ". " + getAdditionalDetails());
    }

    @Override
    public boolean isType(SellerType type) {
        return sellerType == type;
    }

    @Override
    public String getAdditionalDetails() {
        return "EntityType: " + entity + ", CustomName: " + customName;
    }

    @Override
    public String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static EntitySeller deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, EntitySeller.class);
    }
}
