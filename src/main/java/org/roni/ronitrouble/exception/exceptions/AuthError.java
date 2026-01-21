package org.roni.ronitrouble.exception.exceptions;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.exception.ErrorInfo;

@Getter
@RequiredArgsConstructor
public enum AuthError implements ErrorInfo {

    NO_AUTH(401, "当前未登录！"),
    ACCOUNT_NOT_FOUND_ERROR(402, "账号或密码错误");

    private final int code;
    private final String message;

}
