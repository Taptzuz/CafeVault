package me.finn.cafevault.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

public class RandomUtil {

    private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom secureRandom = new SecureRandom();

    static {
        alphabet = alphabet + alphabet.toLowerCase();
    }

    public static @NotNull String randomString(int strLength) {
        StringBuilder stringBuilder = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++)
            stringBuilder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        return stringBuilder.toString();
    }

}
