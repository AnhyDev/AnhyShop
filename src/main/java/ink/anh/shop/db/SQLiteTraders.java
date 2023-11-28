package ink.anh.shop.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ink.anh.shop.AnhyShop;
import ink.anh.shop.trading.Trade;
import ink.anh.shop.trading.Trader;

public class SQLiteTraders extends SQLite {

    public SQLiteTraders(AnhyShop instance) {
        super(instance);
    }

    public void delete(Trader trader) {
        String sql = "DELETE FROM shop WHERE key = ?";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trader.getKey());
            pstmt.executeUpdate();
            shopPlugin.getServer().getLogger().info("shop_removed_from_database: " + trader.getName() + ", key: " + trader.getKey());

        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
    }

    public List<Trader> getTradesmansDB() {
        List<Trader> trmans = new ArrayList<>();
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + shop + ";")) {
            if (ps == null) return null;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Trader trm = new Trader(
                        rs.getString("key"),
                        rs.getString("name"),
                        getTrades(rs.getString("trades")));
                trmans.add(trm);
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
        return trmans;
    }

    public void setTradesmanDB(Trader trader) {
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO " + shop
                     + " (key,name,trades) VALUES(?,?,?)")) {
            ps.setString(1, trader.getKey());
            ps.setString(2, trader.getName());
            ps.setString(3, setTrades(trader.getTrades()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
    }

    public List<Trade> getTrades(String serializedTradesArray) {
        Gson gson = new Gson();
        List<String> serializedTrades = gson.fromJson(serializedTradesArray, new TypeToken<List<String>>() {}.getType());
        List<Trade> trades = new ArrayList<>();

        for (String serializedTrade : serializedTrades) {
            trades.add(Trade.deserializeFromJson(serializedTrade));
        }

        return trades;
    }

    public String setTrades(List<Trade> trades) {
        List<String> serializedTrades = new ArrayList<>();

        for (Trade trade : trades) {
            serializedTrades.add(trade.serializeToJson());
        }
        Gson gson = new Gson();
        String serializedTradesArray = gson.toJson(serializedTrades);

        return serializedTradesArray;
    }
}
