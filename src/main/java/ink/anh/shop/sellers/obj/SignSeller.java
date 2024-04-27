package ink.anh.shop.sellers.obj;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;

public class SignSeller extends AbstractSeller {
    private SellerType sellerType;
    private String[] signTexts;

    public SignSeller(String[] signTexts) {
        this.sellerType = SellerType.SIGN;
        this.signTexts = signTexts;
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
        return "SignTexts: " + String.join("|", signTexts);
    }

    @Override
    public String serialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static SignSeller deserialize(String serializedData) {
        Gson gson = new Gson();
        SignSeller seller = gson.fromJson(serializedData, SignSeller.class);
        seller.setSerializeKey();
        return seller;
    }
}
