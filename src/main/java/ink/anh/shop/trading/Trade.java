package ink.anh.shop.trading;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ink.anh.shop.items.ItemStackSerializer;


public class Trade {

	private String id;
	private ItemStack item1;
	private ItemStack item2;
	private ItemStack rewardItem;

	public Trade(String id, ItemStack item1, ItemStack item2, ItemStack rewardItem) {
		this.id = id;
		this.item1 = item1;
		this.item2 = item2;
		this.rewardItem = rewardItem;
	}

	public Trade(ItemStack item1, ItemStack item2, ItemStack rewardItem) {
		this.id = UUID.randomUUID().toString();
		this.item1 = item1;
		this.item2 = item2;
		this.rewardItem = rewardItem;
	}
	
	public Trade(Trade trade) {
		this.item1 = trade.getItem1();
		this.item2 = trade.getItem2();
		this.rewardItem = trade.getRewardItem();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasItem2() {
		return this.item2 != null;
	}

	public ItemStack getItem1() {
		return this.item1;
	}
	
	public void setItem1(ItemStack item) {
		this.item1 = item;
	}
	
	public void setItem1(ItemStack item, int amount) {
		item.setAmount(amount);
		this.item1 = item;
	}

	public ItemStack getItem2() {
		return this.item2;
	}
	
	public void setItem2(ItemStack item) {
		this.item2 = item;
	}
	
	public void setItem2(ItemStack item, int amount) {
		item.setAmount(amount);
		this.item2 = item;
	}

	public ItemStack getRewardItem() {
		return this.rewardItem;
	}
	public void setRewardItem(ItemStack item) {
		this.rewardItem = item;
	}
	
	public void setRewardItem(ItemStack item, int amount) {
		item.setAmount(amount);
		this.rewardItem = item;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item1 == null) ? 0 : item1.hashCode());
		result = prime * result + ((item2 == null) ? 0 : item2.hashCode());
		result = prime * result + ((rewardItem == null) ? 0 : rewardItem.hashCode());
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
		Trade other = (Trade) obj;
		if (item1 == null) {
			if (other.item1 != null)
				return false;
		} else if (!item1.equals(other.item1))
			return false;
		if (item2 == null) {
			if (other.item2 != null)
				return false;
		} else if (!item2.equals(other.item2))
			return false;
		if (rewardItem == null) {
			if (other.rewardItem != null)
				return false;
		} else if (!rewardItem.equals(other.rewardItem))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String s = "";
		if (item2 != null && item2.getType() != Material.AIR) s = ",\"item2\":" + item2;
		return "{\"id\":" + id + ",\"item1\":" + item1 + s + ",\"rewardItem\":" + rewardItem + "}";
	}

	public String serializeToJson() {
	    Gson gson = new Gson();
	    JsonObject jsonObject = new JsonObject();

	    jsonObject.addProperty("id", id);
	    jsonObject.addProperty("item1", ItemStackSerializer.serializeItemStack(item1));
	    if (item2 != null) {
	        jsonObject.addProperty("item2", ItemStackSerializer.serializeItemStack(item2));
	    }
	    jsonObject.addProperty("rewardItem", ItemStackSerializer.serializeItemStack(rewardItem));

	    String json = gson.toJson(jsonObject);
	    return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
	}
	
	public static Trade deserializeFromJson(String json) {
		try {
            return deserializeBase64FromJson(json);
        } catch (IllegalArgumentException e) {
            try {
                return deserialize(json);
            } catch (JsonSyntaxException e1) {
                e1.printStackTrace();
                return null;
            }
        }
	}

    public static Trade deserializeBase64FromJson(String base64Json) {
        String json = new String(Base64.getDecoder().decode(base64Json), StandardCharsets.UTF_8);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : null;
        ItemStack item1 = ItemStackSerializer.deserializeItemStack(jsonObject.get("item1").getAsString());
        ItemStack item2 = jsonObject.has("item2") ? ItemStackSerializer.deserializeItemStack(jsonObject.get("item2").getAsString()) : null;
        ItemStack rewardItem = ItemStackSerializer.deserializeItemStack(jsonObject.get("rewardItem").getAsString());

        return id != null ? new Trade(id, item1, item2, rewardItem) : new Trade(item1, item2, rewardItem);
    }

    public static Trade deserialize(String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : null;
        ItemStack item1 = ItemStackSerializer.deserializeItemStack(jsonObject.get("item1").getAsString());
        ItemStack item2 = jsonObject.has("item2") ? ItemStackSerializer.deserializeItemStack(jsonObject.get("item2").getAsString()) : null;
        ItemStack rewardItem = ItemStackSerializer.deserializeItemStack(jsonObject.get("rewardItem").getAsString());

        return id != null ? new Trade(id, item1, item2, rewardItem) : new Trade(item1, item2, rewardItem);
    }
}
