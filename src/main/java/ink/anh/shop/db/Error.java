package ink.anh.shop.db;

import java.util.logging.Level;

import ink.anh.shop.AnhyShop;

public class Error {
    public static void execute(AnhyShop shopPlugin, Exception ex){
    	shopPlugin.getLogger().log(Level.SEVERE, "Не удалось выполнить запрос MySQL: ", ex);     
    }
    public static void close(AnhyShop shopPlugin, Exception ex){
    	shopPlugin.getLogger().log(Level.SEVERE, "Не удалось закрыть соединение MySQL: ", ex);
    }
}