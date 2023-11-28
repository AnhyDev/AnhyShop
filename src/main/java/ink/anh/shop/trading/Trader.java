package ink.anh.shop.trading;

import java.util.List;
import java.util.Objects;

public class Trader {

	private String key;
	private String name;
	private List<Trade> trades;

	public Trader(String key, String name, List<Trade> trades) {
		this.key = key;
		this.name = name;
		this.trades = trades;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	public void addTrade(Trade trade) {
		if (trades.contains(trade)) {
			trades.remove(trade);
		}
		trades.add(trade);
	}
	
	public void removeTrade(Trade trade) {
		if (trades.contains(trade)) {
			trades.remove(trade);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trader other = (Trader) obj;
		return Objects.equals(key, other.key);
	}
	
    @Override
    public String toString() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"key\": \"").append(key).append("\",\n");
        jsonBuilder.append("  \"name\": \"").append(name).append("\",\n");
        jsonBuilder.append("  \"trades\": [\n");

        for (int i = 0; i < trades.size(); i++) {
            Trade trade = trades.get(i);
            jsonBuilder.append(trade.serializeToJson()); // Використання серіалізованого представлення trade
            if (i < trades.size() - 1) {
                jsonBuilder.append(",\n");
            }
        }

        jsonBuilder.append("\n  ]\n");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
