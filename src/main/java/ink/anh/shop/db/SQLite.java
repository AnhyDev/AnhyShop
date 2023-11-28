package ink.anh.shop.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import ink.anh.shop.AnhyShop;


public class SQLite extends Database{

	private String dbname;

    public SQLite(AnhyShop shopPlugin){
        super(shopPlugin);
        dbname =  "shop";
    }
    		
    public Connection getSQLConnection() {
        File dataFolder = new File(AnhyShop.getInstance().getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
            	AnhyShop.getInstance().getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
        	AnhyShop.getInstance().getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
        	AnhyShop.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
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
