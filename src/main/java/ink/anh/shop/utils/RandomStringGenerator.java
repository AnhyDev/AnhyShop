package ink.anh.shop.utils;

import java.security.SecureRandom;
import java.time.Instant;

public class RandomStringGenerator {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + NUMBER;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        long salt = Instant.now().toEpochMilli(); // використовуємо поточний час як сіль
        random.setSeed(random.nextLong() ^ salt); // змішуємо сіль з випадковим числом

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String randomString = generateRandomString(8);
        System.out.println("Random String: " + randomString);
    }
}
