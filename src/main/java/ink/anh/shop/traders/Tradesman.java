package ink.anh.shop.traders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tradesman {

	private String name;
	private List<Trade> trades = new ArrayList<>();

	public Tradesman(String name, List<Trade> trades) {
		super();
		this.name = name;
		this.setTrades(trades);
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tradesman other = (Tradesman) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tradesman [name=" + name + ", trades=" + Arrays.asList(trades) + "]";
	}
	
}
