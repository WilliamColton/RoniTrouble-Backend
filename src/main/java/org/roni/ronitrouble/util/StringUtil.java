package org.roni.ronitrouble.util;

public class StringUtil {

    public static String capitalise(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    public static String uncapitalise(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

}
