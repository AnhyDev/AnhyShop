package ink.anh.shop.db;

public class Errors {

<<<<<<< HEAD
<<<<<<< HEAD
    public static String sqlConnectionExecute() {
        return "MySQL query failed: ";
    }
    public static String sqlConnectionClose() {
        return "Failed to close MySQL connection: ";
    }
    public static String noSQLConnection() {
        return "Failed to get MYSQL connection: ";
    }
    public static String noTableFound() {
=======
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
>>>>>>> branch 'main' of https://github.com/AnhyDev/AnhyShop.git
=======
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
>>>>>>> branch 'main' of https://github.com/AnhyDev/AnhyShop.git
        return "Database Error: Table not found";
    }
}
