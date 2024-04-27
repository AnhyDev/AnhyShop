package ink.anh.shop.sellers.obj;

import org.bukkit.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;

public class MechanicalSeller extends AbstractSeller {
    private SellerType sellerType;
    private Location loc;

    public MechanicalSeller(SellerType type, Location loc) {
        this.sellerType = type;
        this.loc = loc;
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
        return "SellerType: " + sellerType.name() + ". Location: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    @Override
    public String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // Використовуємо вбудований метод serialize() класу Location
        Map<String, Object> locMap = loc.serialize();
        String locationJson = gson.toJson(locMap);

        // Serialize other parts of MechanicalSeller
        return gson.toJson(new SerializedSeller(sellerType.name(), locationJson));
    }

    public static MechanicalSeller deserialize(String serializedData) {
        Gson gson = new Gson();
        SerializedSeller serializedSeller = gson.fromJson(serializedData, SerializedSeller.class);
        Type typeToken = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> locationMap = gson.fromJson(serializedSeller.locationJson, typeToken);

        Location location = Location.deserialize(locationMap);
        SellerType type = SellerType.valueOf(serializedSeller.type);

        return new MechanicalSeller(type, location);
    }

    private static class SerializedSeller {
        String type;
        String locationJson;

        SerializedSeller(String type, String locationJson) {
            this.type = type;
            this.locationJson = locationJson;
        }
    }
}
