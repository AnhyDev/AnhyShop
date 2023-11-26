package ink.anh.shop.db;

public class Errors {

    public static String sqlConnectionExecute(){
        return "Не удалось выполнить запрос MySQL: ";
    }
    public static String sqlConnectionClose(){
        return "Не удалось закрыть соединение MySQL: ";
    }
    public static String noSQLConnection(){
        return "Не удалось получить соединение MYSQL: ";
    }
    public static String noTableFound(){
        return "Ошибка базы данных: таблица не найдено";
    }
}
