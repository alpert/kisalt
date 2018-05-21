package com.github.alpert.utils.hash;

public class Utils {

    public static final String ALPHABET     = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static       int    ALPHABET_LEN = ALPHABET.length();

    private static final char[] BASE62 = ALPHABET.toCharArray();

    public static String base62(int value) {
        final StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, BASE62[value % ALPHABET_LEN]);
            value /= ALPHABET_LEN;
        } while (value > 0);
        return sb.toString();
    }
}
