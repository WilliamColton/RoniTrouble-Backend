package org.roni.ronitrouble.util;

public class UserContextUtil {

    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static Integer getCurrentUserId() {
        return threadLocal.get();
    }

    public static void setCurrentUserId(Integer userId) {
        threadLocal.set(userId);
    }

    public static void clearUserId() {
        threadLocal.remove();
    }

}
