package ink.anh.shop.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import ink.anh.api.messages.Logger;
import ink.anh.shop.AnhyShop;


public class SQLite extends Database{

	private String dbname;

    public SQLite(AnhyShop shopPlugin){
        super(shopPlugin);
        dbname =  "shop";
    }
    		
    public Connection getSQLConnection() {

        File dataFolder = shopPlugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs(); // Спроба створити папку
            if (!created) {
                Logger.error(shopPlugin, "Could not create plugin directory: " + dataFolder.getPath());
                return null;
            }
        }
        File dataBase= new File(dataFolder, dbname+"s.db");
        if (!dataBase.exists()){
            try {
            	dataBase.createNewFile();
            } catch (IOException e) {
            	shopPlugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+"s.db");
            }
        }
        try {
            if(connection!=null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase);
            return connection;
        } catch (SQLException ex) {
        	shopPlugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
        	shopPlugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
 
    public void load() {
        connection = getSQLConnection();     
        try {
            Statement s = connection.createStatement();

            s.executeUpdate("CREATE TABLE IF NOT EXISTS shop ("
/* 1*/            	+ "`key` TEXT NOT NULL UNIQUE,"
/* 2*/            	+ "`name` TEXT,"
/* 3*/            	+ "`trades` TEXT, PRIMARY KEY (`key`));");

            s.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

}
