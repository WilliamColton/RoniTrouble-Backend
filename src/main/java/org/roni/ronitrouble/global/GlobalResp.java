package org.roni.ronitrouble.global;

public record GlobalResp<T>(T data, int code, String message) {

    public static <T> GlobalResp<T> success(T data) {
        return new GlobalResp<>(data, 200, "操作成功");
    }

    public static GlobalResp<Object> error(int code, String message) {
        return new GlobalResp<>(null, code, message);
    }

}
