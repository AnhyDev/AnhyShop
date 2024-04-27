package ink.anh.shop.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import ink.anh.shop.AnhyShop;
import ink.anh.shop.sellers.obj.AbstractSeller;
import ink.anh.shop.sellers.obj.EntitySeller;
import ink.anh.shop.sellers.obj.MechanicalSeller;
import ink.anh.shop.sellers.obj.SellerType;
import ink.anh.shop.sellers.obj.SignSeller;
import ink.anh.shop.sellers.obj.VillagerSeller;
import ink.anh.shop.trading.Trader;

public class SQLiteSellers extends SQLite {

    public SQLiteSellers(AnhyShop instance) {
        super(instance);
    }

    public void saveSeller(AbstractSeller seller) {
    	Trader trader = seller.getTrader();
    	if (trader == null) return;
    	String traderKey = trader.getKey();
    	int key = seller.hashCode();
    	String sql = "INSERT OR REPLACE INTO sellerList (key, trader, type, seller) VALUES (?, ?, ?, ?)";
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, key);
            ps.setString(2, traderKey);
            ps.setString(3, seller.getSellerType().getName());
            ps.setString(4, seller.getSerializeKey());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                shopPlugin.getServer().getLogger().info("Seller added to database with key: " + key);
            } else {
                shopPlugin.getServer().getLogger().info("Failed to add seller: " + trader);
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
    }

    public void deleteSeller(Integer key) {
        String sql = "DELETE FROM sellerList WHERE key = ?";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, key);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                shopPlugin.getServer().getLogger().info("Seller removed from database for key: " + key);
            } else {
                shopPlugin.getServer().getLogger().info("No seller found to remove for key: " + key);
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
    }

    public void deleteSellers(List<Integer> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        String inClause = keys.stream()
                              .map(String::valueOf)
                              .collect(Collectors.joining(", "));

        String sql = "DELETE FROM sellerList WHERE key IN (" + inClause + ")";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                shopPlugin.getServer().getLogger().info(affectedRows + " sellers removed from the database.");
            } else {
                shopPlugin.getServer().getLogger().info("No sellers found to remove with provided keys.");
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL batch delete", ex);
        }
    }

    public Map<Integer, AbstractSeller> getSellers() {
        Map<Integer, AbstractSeller> sellers = new HashMap<>();
        String sql = "SELECT key, trader, type, seller FROM sellerList;";
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int key = rs.getInt("key");
                String traderKey = rs.getString("trader");
                String type = rs.getString("type");
                String seller = rs.getString("seller");
                sellers.put(key, getSeller(traderKey, type, seller));
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
        return sellers;
    }

    public AbstractSeller getSellerByKey(Integer key) {
        String sql = "SELECT trader, type, seller FROM sellerList WHERE key = ?";
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String traderKey = rs.getString("trader");
                String type = rs.getString("type");
                String serializedData = rs.getString("seller");
                return getSeller(traderKey, type, serializedData);
            } else {
                shopPlugin.getServer().getLogger().info("No seller found for key: " + key);
                return null;
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
            return null;
        }
    }

    public AbstractSeller getSeller(String traderKey, String typeName, String serializedData) {
        AbstractSeller seller = null;
        try {
            SellerType type = SellerType.valueOf(typeName.toUpperCase());
            switch (type) {
                case ENTITY:
                    seller = EntitySeller.deserialize(serializedData);
                    break;
                case VILLAGER:
                case WANDERING_TRADER:
                    seller = VillagerSeller.deserialize(serializedData);
                    break;
                case SIGN:
                    seller = SignSeller.deserialize(serializedData);
                    break;
                case BUTTON:
                case LEVER:
                case DOOR:
                    seller = MechanicalSeller.deserialize(serializedData);
                    break;
			default:
				break;
            }
        } catch (IllegalArgumentException e) {
            shopPlugin.getLogger().log(Level.SEVERE, "Unknown SalerType: " + typeName, e);
            return null;
        }

        if (seller != null) {
            Trader trader = new Trader(traderKey, traderKey, new ArrayList<>());
            seller.setTrader(trader);
        	
        }
        return seller;
    }
}
