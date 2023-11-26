package ink.anh.shop.lang;

import ink.anh.lingo.api.lang.LanguageManager;
import ink.anh.shop.AnhyShop;

public class LangMessage extends LanguageManager {

    private static LangMessage instance = null;
    private static final Object LOCK = new Object();

    private LangMessage(AnhyShop shopPlugin) {
        super( shopPlugin, "lang");
    }

    public static LangMessage getInstance(AnhyShop shopPlugin) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new LangMessage(shopPlugin);
                }
            }
        }
        return instance;
    }
}
