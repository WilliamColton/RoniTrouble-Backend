package org.roni.ronitrouble.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MapperUtil {

    public static <T> T mapper(Class<T> descClass, Object object) {
        T desc = null;
        try {
            desc = descClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        for (Method method : object.getClass().getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                try {
                    Method descMethod = desc.getClass().getDeclaredMethod(methodName.replaceFirst("g", "s"), method.getReturnType());
                    descMethod.invoke(desc, method.invoke(object));
                } catch (InvocationTargetException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException _) {
                }
            }
        }
        return desc;
    }

}
