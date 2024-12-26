package ink.anh.shop.db;

import java.util.logging.Level;

import ink.anh.shop.AnhyShop;

public class Error {
    public static void execute(AnhyShop shopPlugin, Exception ex){
    	shopPlugin.getLogger().log(Level.SEVERE, "MySQL query failed: ", ex);     
    }
    public static void close(AnhyShop shopPlugin, Exception ex){
    	shopPlugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}