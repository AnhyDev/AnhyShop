package ink.anh.shop.sellers.obj;

import org.bukkit.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;

public class MechanicalSeller extends AbstractSeller {
    private SellerType sellerType;
    private Location loc;

    public MechanicalSeller(SellerType type, Location loc) {
        this.sellerType = type;
        this.loc = loc;
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
        return "Location: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    @Override
    public String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static MechanicalSeller deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, MechanicalSeller.class);
    }
}
