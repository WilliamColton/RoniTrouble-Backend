package org.roni.ronitrouble.exception.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.exception.ErrorInfo;

@Getter
@RequiredArgsConstructor
public enum OtherError implements ErrorInfo {

    NETWORK_ERROR(1000, "网络错误");

    private final int code;
    private final String message;

}
