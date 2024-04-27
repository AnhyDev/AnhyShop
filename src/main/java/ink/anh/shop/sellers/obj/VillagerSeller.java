package ink.anh.shop.sellers.obj;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;

public class VillagerSeller extends AbstractSeller {
    private SellerType sellerType;
    private EntityType entityType;
    private Profession profession;
    private int level;

    public VillagerSeller(Profession profession, int level) {
        this.sellerType = SellerType.VILLAGER;
        this.entityType = EntityType.VILLAGER;
        this.profession = profession;
        this.level = level;
        setSerializeKey();
    }

    public VillagerSeller() {
        this.sellerType = SellerType.WANDERING_TRADER;
        this.entityType = EntityType.WANDERING_TRADER;
        setSerializeKey();
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
        return "EntityType: " + entityType.name() + (profession != null ? ". " + profession.name() : "") + (level > 0 ? ". Level = " + level : "");
    }

    @Override
    public String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static VillagerSeller deserialize(String serializedData) {
        Gson gson = new Gson();
        VillagerSeller seller = gson.fromJson(serializedData, VillagerSeller.class);
        seller.setSerializeKey();
        return seller;
    }
}
