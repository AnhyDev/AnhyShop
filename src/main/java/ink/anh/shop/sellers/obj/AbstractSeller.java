package ink.anh.shop.sellers.obj;

import ink.anh.shop.trading.Trader;

public abstract class AbstractSeller {
	
    transient private Trader trader;  // Gson ігнорує це поле під час серіалізації
    transient private String serializeKey;  // Gson ігнорує це поле під час серіалізації

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
        result = 31 * result + (serializeKey != null ? serializeKey.hashCode() : 0);
        return (result & Integer.MAX_VALUE) + 17; // Забезпечення позитивного значення
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractSeller other = (AbstractSeller) obj;
        return serializeKey.equals(other.serializeKey);
    }
    
	public String getSerializeKey() {
		return serializeKey;
	}
	
	public void setSerializeKey(String key) {
		this.serializeKey = key;
	}
	
	public void setSerializeKey() {
		this.serializeKey = serialize();
	}
}
