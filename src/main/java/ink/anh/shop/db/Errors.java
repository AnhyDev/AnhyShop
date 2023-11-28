package ink.anh.shop.db;

public class Errors {

    public static String sqlConnectionExecute(){
        return "MySQL query failed: ";
    }
    public static String sqlConnectionClose(){
        return "Failed to close MySQL connection: ";
    }
    public static String noSQLConnection(){
        return "Failed to get MYSQL connection: ";
    }
    public static String noTableFound(){
        return "Database Error: Table not found";
    }
}
