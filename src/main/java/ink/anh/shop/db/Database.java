package ink.anh.shop.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import ink.anh.shop.AnhyShop;

public abstract class Database {

	public AnhyShop shopPlugin;
    protected Connection connection;
    public String shop;

    public Database(AnhyShop instance){
    	this.shopPlugin = instance;
        this.shop = "shop";
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    /**
     * Инициализация БД
     */
    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + shop + " WHERE key = ?");
            ps.setString(1, "");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
         
        } catch (SQLException ex) {                                                                                                                                                                                                                                                                                                                                                                      
            shopPlugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    /**
     * Закрыть подключение к БД
     * @param ps
     * @param rs
     */
    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(shopPlugin, ex);
        }
    }
 
}
