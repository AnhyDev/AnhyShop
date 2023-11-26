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
import ink.anh.shop.traders.Trade;
import ink.anh.shop.traders.Tradesman;

public class MyTradeSQLite extends SQLite {

    public MyTradeSQLite(AnhyShop instance) {
        super(instance);
    }

    public void delete(Tradesman trm) {
        String sql = "DELETE FROM AnhyShop_trades WHERE name = ?";
        try (Connection conn = getSQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trm.getName());
            pstmt.executeUpdate();
            shopPlugin.getServer().getLogger().info("из БД удален: " + trm.getName());

        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
    }

    public List<Tradesman> getTradesmansDB() {
        List<Tradesman> trmans = new ArrayList<>();
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + shop + ";")) {
            if (ps == null) return null;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tradesman trm = new Tradesman(
                        rs.getString("name"),
                        getTrades(rs.getString("trades")));
                trmans.add(trm);
            }
        } catch (SQLException ex) {
            shopPlugin.getLogger().log(Level.SEVERE, "Error executing SQL query", ex);
        }
        return trmans;
    }

    public void setTradesmanDB(Tradesman trm) {
        try (Connection conn = getSQLConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO " + shop
                     + " (name,trades,col01,col02,col03) VALUES(?,?,?,?,?)")) {
            ps.setString(1, trm.getName());
            ps.setString(2, setTrades(trm.getTrades()));
            ps.setString(3, null);
            ps.setString(4, null);
            ps.setInt(5, 0);
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
