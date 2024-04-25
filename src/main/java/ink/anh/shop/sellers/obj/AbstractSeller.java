package ink.anh.shop.sellers.obj;

import ink.anh.shop.trading.Trader;

public abstract class AbstractSeller {
	
    transient private Trader trader;  // Gson ігнорує це поле під час серіалізації

    public abstract SellerType getSellerType();
    public abstract void performAction();
    public abstract boolean isType(SellerType type);
    public abstract String getAdditionalDetails();
    public abstract String serialize();

	public static AbstractSeller deserialize(String serializedData) {
        return null;
	}

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }
	
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (getSellerType() != null ? getSellerType().hashCode() : 0);
        result = 31 * result + (getAdditionalDetails() != null ? getAdditionalDetails().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractSeller other = (AbstractSeller) obj;
        if (getSellerType() != other.getSellerType()) {
            return false;
        }
        if (getAdditionalDetails() == null) {
            return other.getAdditionalDetails() == null;
        } else {
            return getAdditionalDetails().equals(other.getAdditionalDetails());
        }
    }
}
