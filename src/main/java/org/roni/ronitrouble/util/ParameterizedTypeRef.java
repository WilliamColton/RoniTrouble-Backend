package org.roni.ronitrouble.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ParameterizedTypeRef<T> {

    private final Type type;

    public ParameterizedTypeRef() {
        Class<?> cls = findParameterizedTypeClass(this.getClass());
        Type genericSuperclass = cls.getGenericSuperclass();
        assert genericSuperclass instanceof ParameterizedType;
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        type = parameterizedType.getActualTypeArguments()[0];
    }

    public static Class<?> findParameterizedTypeClass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (parent == Object.class) {
            throw new RuntimeException("该类并未继承 ParameterizedTypeRef");
        }
        if (parent == ParameterizedTypeRef.class) {
            return child;
        }
        return findParameterizedTypeClass(parent);
    }

    public Type getType() {
        return type;
    }

    public String getClassName() {
        return type.getClass().getSimpleName();
    }

    public String getUncapitaliseClassName() {
        return StringUtil.uncapitalise(getClassName());
    }

}
